package com.example.eventdriven.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import com.example.eventdriven.dto.OrderCreatedEvent;
import com.example.eventdriven.service.FailedEventService;
import com.example.eventdriven.service.InventoryService;
import com.example.eventdriven.service.OrderCoordinationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class InventoryConsumerRoute extends RouteBuilder {

    private final InventoryService inventoryService;
    private final FailedEventService failedEventService;
    private final OrderCoordinationService orderCoordinationService;
    private final ObjectMapper objectMapper;

    public InventoryConsumerRoute(
            InventoryService inventoryService,
            FailedEventService failedEventService,
            OrderCoordinationService orderCoordinationService,
            ObjectMapper objectMapper) {

        this.inventoryService = inventoryService;
        this.failedEventService = failedEventService;
        this.orderCoordinationService = orderCoordinationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure() {

        // Retry and DLQ handling
        onException(Exception.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(2000)
            .useOriginalMessage()
            .handled(true)
            .log(LoggingLevel.ERROR,
                "Inventory processing failed after retries. Sending event to DLQ: ${exception.message}")
            .process(exchange -> {

                String originalMessage =
                    exchange.getIn().getBody(String.class);

                OrderCreatedEvent event =
                    objectMapper.readValue(
                        originalMessage,
                        OrderCreatedEvent.class
                    );

                Exception exception = exchange.getProperty(
                    Exchange.EXCEPTION_CAUGHT,
                    Exception.class
                );

                failedEventService.recordFailure(
                    event.getOrderId(),
                    "INVENTORY",
                    exception.getMessage()
                );

                // Notify coordinator about technical failure
                orderCoordinationService.updateInventoryStatus(
                    event.getOrderId(), "FAILED"
                );
            })
            .to("kafka:inventory-service-dlq?brokers=localhost:9092");

        // Kafka Inventory Consumer
        from("kafka:order-created-events"
                + "?brokers=localhost:9092"
                + "&groupId=inventory-service")
            .routeId("inventory-consumer-route")
            .unmarshal().json(
                org.apache.camel.model.dataformat.JsonLibrary.Jackson,
                OrderCreatedEvent.class
            )

            .process(exchange -> {

                OrderCreatedEvent event =
                    exchange.getIn().getBody(OrderCreatedEvent.class);

                String status =
                    inventoryService.reserveInventory(event);

                exchange.setProperty("inventoryStatus", status);

                // Update overall order status
                if (!"DUPLICATE".equals(status)) {

                    orderCoordinationService.updateInventoryStatus(
                        event.getOrderId(), status
                    );
                }

                if ("RESERVED".equals(status)) {

                    log.info(
                        "Inventory successfully reserved for order: {}",
                        event.getOrderId()
                    );

                } else if ("OUT_OF_STOCK".equals(status)) {

                    log.warn(
                        "Insufficient stock for order: {}",
                        event.getOrderId()
                    );

                } else if ("PRODUCT_NOT_FOUND".equals(status)) {

                    log.warn(
                        "Product not found for order: {}",
                        event.getOrderId()
                    );

                } else if ("DUPLICATE".equals(status)) {

                    log.info(
                        "Duplicate inventory event ignored for order: {}",
                        event.getOrderId()
                    );
                }
            });
    }
}
