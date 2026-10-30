
package com.example.restkafka.route;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.example.restkafka.bean.OrderMapper;
import com.example.restkafka.dto.OrderRequest;
import com.example.restkafka.dto.OrderResponse;
import com.example.restkafka.entity.OrderRecord;
import com.example.restkafka.exception.KafkaPublishException;
import com.example.restkafka.service.OrderService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Component
public class OrderProcessingRoute extends RouteBuilder {

    private final Validator validator;
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderProcessingRoute(
            Validator validator,
            OrderService orderService,
            OrderMapper orderMapper) {

        this.validator = validator;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @Override
    public void configure() {

        // Invalid request
        onException(ConstraintViolationException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setBody(simple(
                "{\"status\":400,\"error\":\"Bad Request\","
                + "\"message\":\"${exception.message}\"}"
            ));

        // Duplicate order
        onException(IllegalArgumentException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(409))
            .setBody(simple(
                "{\"status\":409,\"error\":\"Conflict\","
                + "\"message\":\"${exception.message}\"}"
            ));

        // Kafka publishing failure
        onException(KafkaPublishException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(503))
            .setBody(simple(
                "{\"status\":503,\"error\":\"Service Unavailable\","
                + "\"message\":\"Order could not be published to Kafka\"}"
            ));

        // Unexpected errors
        onException(Exception.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
            .setBody(simple(
                "{\"status\":500,\"error\":\"Internal Server Error\","
                + "\"message\":\"An unexpected error occurred\"}"
            ));

       
        // ROUTE 1: REST ORDER PROCESSING
        
        from("direct:process-order")
            .routeId("rest-order-processing")

            // Validate order and generate correlation ID
            .process(exchange -> {

                OrderRequest order = exchange.getMessage()
                        .getBody(OrderRequest.class);

                Set<ConstraintViolation<OrderRequest>> violations =
                        validator.validate(order);

                if (!violations.isEmpty()) {

                    String errors = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining(", "));

                    throw new ConstraintViolationException(
                            errors, violations);
                }

                String correlationId =
                        UUID.randomUUID().toString();

                exchange.getMessage().setHeader(
                        "correlationId", correlationId);

                exchange.getMessage().setHeader(
                        "orderId", order.getOrderId());

                log.info("Received order: {}, Correlation ID: {}",
                        order.getOrderId(), correlationId);
            })

            // Register order in database
            .process(exchange -> {

                String orderId = exchange.getMessage()
                        .getHeader("orderId", String.class);

                String correlationId = exchange.getMessage()
                        .getHeader("correlationId", String.class);

                orderService.registerOrder(orderId, correlationId);

                log.info("Order registered as PENDING: {}", orderId);
            })

            // Prepare Kafka message
            .process(exchange -> {

                OrderRequest order = exchange.getMessage()
                        .getBody(OrderRequest.class);

                String correlationId = exchange.getMessage()
                        .getHeader("correlationId", String.class);

                exchange.getMessage().setBody(
                        orderMapper.toKafkaMessage(order, correlationId));
            })

            // Publish message to Kafka
            .doTry()

                .marshal().json(JsonLibrary.Jackson)

                .setHeader("kafka.KEY", header("orderId"))

                .to("kafka:{{app.kafka.topic}}"
                        + "?brokers={{camel.component.kafka.brokers}}"
                        + "&requestRequiredAcks=all"
                        + "&enableIdempotence=true")

                // Update status after successful publishing
                .process(exchange -> {

                    String orderId = exchange.getMessage()
                            .getHeader("orderId", String.class);

                    orderService.markPublished(orderId);

                    log.info("Order successfully published to Kafka: {}",
                            orderId);
                })

                // Successful REST response
                .setBody(exchange -> new OrderResponse(
                        "Order accepted for processing",
                        exchange.getMessage().getHeader(
                                "orderId", String.class),
                        exchange.getMessage().getHeader(
                                "correlationId", String.class),
                        "PUBLISHED"
                ))

                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))

            // Handle Kafka publishing failure
            .doCatch(Exception.class)

                .process(exchange -> {

                    String orderId = exchange.getMessage()
                            .getHeader("orderId", String.class);

                    Exception exception = exchange.getProperty(
                            Exchange.EXCEPTION_CAUGHT, Exception.class);

                    orderService.markFailed(
                            orderId,
                            exception != null
                                    ? exception.getMessage()
                                    : "Kafka publishing failed");

                    log.error(
                            "Kafka publishing failed. Order ID: {}, Correlation ID: {}",
                            orderId,
                            exchange.getMessage().getHeader("correlationId"),
                            exception
                    );
                })

                .throwException(new KafkaPublishException(
                        "Unable to publish order to Kafka"))

            .end();


        // ROUTE 2: GET ORDER STATUS

        from("direct:get-order-status")
        .routeId("get-order-status")

        .log("GET request received. Headers: ${headers}")

        .process(exchange -> {
            String orderId = exchange.getMessage()
                .getHeader("orderId", String.class);

            log.info("Fetching order status for: {}", orderId);

            OrderRecord record = orderService.getOrderRecord(orderId);

            log.info("Order record found: {}", record.getOrderId());

            Map<String, Object> response = new LinkedHashMap<>();

            response.put("orderId", record.getOrderId());
            response.put("correlationId", record.getCorrelationId());
            response.put("status", record.getStatus());
            response.put("createdAt", record.getCreatedAt());
            response.put("updatedAt", record.getUpdatedAt());
            response.put("errorMessage", record.getErrorMessage());

            exchange.getMessage().setBody(response);
        })

        .log("GET response prepared: ${body}");
    }
}
