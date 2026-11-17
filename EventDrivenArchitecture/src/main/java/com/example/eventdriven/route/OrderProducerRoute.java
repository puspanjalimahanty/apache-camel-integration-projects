package com.example.eventdriven.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.eventdriven.dto.OrderCreatedEvent;
import com.example.eventdriven.dto.OrderRequest;
import com.example.eventdriven.model.OrderRecord;
import com.example.eventdriven.model.ProductStock;
import com.example.eventdriven.repository.OrderRecordRepository;
import com.example.eventdriven.repository.ProductStockRepository;

@Component
public class OrderProducerRoute extends RouteBuilder {

    private final OrderRecordRepository orderRecordRepository;
    private final ProductStockRepository productStockRepository;

    public OrderProducerRoute(
            OrderRecordRepository orderRecordRepository,
            ProductStockRepository productStockRepository) {

        this.orderRecordRepository = orderRecordRepository;
        this.productStockRepository = productStockRepository;
    }

    @Override
    public void configure() {

        restConfiguration()
            .component("platform-http")
            .bindingMode(
                org.apache.camel.model.rest.RestBindingMode.json);

        rest("/api/orders")
            .post()
            .type(OrderRequest.class)
            .to("direct:publish-order-event");

        from("direct:publish-order-event")
            .routeId("order-event-producer-route")

            .process(exchange -> {

                OrderRequest request =
                    exchange.getIn().getBody(OrderRequest.class);

                if (request == null
                        || request.getOrderId() == null
                        || request.getOrderId().isBlank()
                        || request.getCustomerName() == null
                        || request.getCustomerName().isBlank()
                        || request.getCustomerEmail() == null
                        || request.getCustomerEmail().isBlank()
                        || request.getProductName() == null
                        || request.getProductName().isBlank()
                        || request.getQuantity() <= 0) {

                    throw new IllegalArgumentException(
                        "Invalid order details");
                }

                // Prevent duplicate order creation
                if (orderRecordRepository.existsById(
                        request.getOrderId())) {

                    throw new IllegalArgumentException(
                        "Order already exists: " + request.getOrderId());
                }

                // Fetch product and its fixed price
                ProductStock product = productStockRepository
                    .findByProductName(request.getProductName())
                    .orElseThrow(() ->
                        new IllegalArgumentException(
                            "Product not found: " + request.getProductName()));

                double fixedUnitPrice =
                    product.getUnitPrice().doubleValue();

                // Calculate amount using the fixed product price
                double totalAmount =
                    request.getQuantity() * fixedUnitPrice;

                // Create initial order record
                OrderRecord orderRecord =
                    new OrderRecord(request.getOrderId());

                orderRecord.setCustomerName(request.getCustomerName());
                orderRecord.setCustomerEmail(request.getCustomerEmail());
                orderRecord.setProductName(product.getProductName());
                orderRecord.setQuantity(request.getQuantity());
                orderRecord.setTotalAmount(totalAmount);

                orderRecordRepository.saveAndFlush(orderRecord);

                log.info(
                    "Order created: orderId={}, product={}, quantity={}, fixedUnitPrice={}, totalAmount={}",
                    request.getOrderId(),
                    product.getProductName(),
                    request.getQuantity(),
                    fixedUnitPrice,
                    totalAmount
                );

                // Create Kafka event
                OrderCreatedEvent event = new OrderCreatedEvent(
                    request.getOrderId(),
                    request.getCustomerName(),
                    request.getCustomerEmail(),
                    product.getProductName(),
                    request.getQuantity(),
                    totalAmount
                );

                exchange.getIn().setBody(event);
            })

            // Publish order event to Kafka
            .marshal().json()

            .to("kafka:order-created-events"
                + "?brokers=localhost:9092")

            .setHeader("Content-Type",
                constant("application/json"))

            .setBody(constant(
                "{\"message\":\"Order accepted and event published successfully\"}"
            ));
    }
}
