package com.example.kafkadb.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.example.kafkadb.dto.OrderMessage;
import com.example.kafkadb.service.OrderService;

@Component
public class KafkaOrderConsumerRoute extends RouteBuilder {

    private final OrderService orderService;

    public KafkaOrderConsumerRoute(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void configure() {

        // Retry failed processing and send exhausted messages to DLQ
        errorHandler(
            deadLetterChannel(
                "kafka:orders.DLQ?brokers={{camel.component.kafka.brokers}}")
                .maximumRedeliveries(3)
                .redeliveryDelay(2000)
                .useOriginalMessage()
        );

        from("kafka:{{app.kafka.topic}}"
                + "?brokers={{camel.component.kafka.brokers}}"
                + "&groupId=order-database-consumer"
                + "&autoOffsetReset=earliest")
            .routeId("kafka-order-database-consumer")

            .log("Received order message from Kafka: ${body}")

            // Convert JSON into OrderMessage DTO
            .unmarshal()
                .json(JsonLibrary.Jackson, OrderMessage.class)

            // Validate and save order
            .bean(orderService, "saveOrder")

            .bean(orderService, "saveOrder")
            .log("Order processing completed");
    }
}
