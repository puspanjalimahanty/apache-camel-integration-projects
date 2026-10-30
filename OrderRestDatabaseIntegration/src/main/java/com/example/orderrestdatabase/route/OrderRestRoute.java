package com.example.orderrestdatabase.route;

import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.example.orderrestdatabase.dto.ApiErrorResponse;
import com.example.orderrestdatabase.dto.OrderRequest;
import com.example.orderrestdatabase.dto.OrderResponse;
import com.example.orderrestdatabase.entity.Order;
import com.example.orderrestdatabase.service.DuplicateOrderException;
import com.example.orderrestdatabase.service.OrderNotFoundException;
import com.example.orderrestdatabase.service.OrderService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Component
public class OrderRestRoute extends RouteBuilder {

    private final OrderService orderService;
    private final Validator validator;

    public OrderRestRoute(
            OrderService orderService,
            Validator validator) {
        this.orderService = orderService;
        this.validator = validator;
    }

    @Override
    public void configure() {

        // Duplicate order
        onException(DuplicateOrderException.class)
            .handled(true)
            .log(LoggingLevel.WARN, "Duplicate order rejected")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(409))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .process(exchange -> {
                Exception ex = exchange.getProperty(
                    Exchange.EXCEPTION_CAUGHT, Exception.class);

                exchange.getMessage().setBody(
                    new ApiErrorResponse(
                        409, "Conflict", ex.getMessage()));
            })
            .marshal().json(JsonLibrary.Jackson);

        // Database constraint violation
        onException(DataIntegrityViolationException.class)
            .handled(true)
            .log(LoggingLevel.WARN, "Database constraint violation")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(409))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .process(exchange -> exchange.getMessage().setBody(
                new ApiErrorResponse(
                    409,
                    "Conflict",
                    "The order conflicts with an existing database record."
                )))
            .marshal().json(JsonLibrary.Jackson);

        // Invalid request or invalid order ID
        onException(IllegalArgumentException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .process(exchange -> {
                Exception ex = exchange.getProperty(
                    Exchange.EXCEPTION_CAUGHT, Exception.class);

                exchange.getMessage().setBody(
                    new ApiErrorResponse(
                        400, "Bad Request", ex.getMessage()));
            })
            .marshal().json(JsonLibrary.Jackson);

        // Order not found
        onException(OrderNotFoundException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .process(exchange -> {
                Exception ex = exchange.getProperty(
                    Exchange.EXCEPTION_CAUGHT, Exception.class);

                exchange.getMessage().setBody(
                    new ApiErrorResponse(
                        404, "Not Found", ex.getMessage()));
            })
            .marshal().json(JsonLibrary.Jackson);

        // Unexpected errors
        onException(Exception.class)
            .handled(true)
            .log(LoggingLevel.ERROR,
                "Order processing failed: ${exception.message}")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .process(exchange -> exchange.getMessage().setBody(
                new ApiErrorResponse(
                    500,
                    "Internal Server Error",
                    "An unexpected error occurred while processing the request."
                )))
            .marshal().json(JsonLibrary.Jackson);

        // REST configuration
        restConfiguration()
            .component("platform-http")
            .bindingMode(RestBindingMode.json);

        // REST endpoints
        rest("/api/orders")
            .post()
                .type(OrderRequest.class)
                .to("direct:create-order")
            .get()
                .to("direct:get-all-orders")
            .get("/{id}")
                .to("direct:get-order-by-id");

        // POST: Create order
        from("direct:create-order")
            .routeId("create-order-route")
            .log(LoggingLevel.INFO, "Received order request")
            .process(exchange -> {

                OrderRequest request =
                    exchange.getMessage().getBody(OrderRequest.class);

                if (request == null) {
                    throw new IllegalArgumentException(
                        "Request body is required");
                }

                Set<ConstraintViolation<OrderRequest>> violations =
                    validator.validate(request);

                if (!violations.isEmpty()) {
                    String message = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .sorted()
                        .reduce((a, b) -> a + "; " + b)
                        .orElse("Invalid order request");

                    throw new IllegalArgumentException(message);
                }

                Order order = new Order();
                order.setOrderNumber(request.getOrderNumber().trim());
                order.setCustomerName(request.getCustomerName().trim());
                order.setProductName(request.getProductName().trim());
                order.setQuantity(request.getQuantity());
                order.setUnitPrice(request.getUnitPrice());

                exchange.getMessage().setBody(order);
            })
            .bean(orderService, "createOrder")
            .process(exchange -> {

                Order savedOrder =
                    exchange.getMessage().getBody(Order.class);

                OrderResponse response = new OrderResponse(
                    savedOrder.getId(),
                    savedOrder.getOrderNumber(),
                    savedOrder.getCustomerName(),
                    savedOrder.getProductName(),
                    savedOrder.getQuantity(),
                    savedOrder.getUnitPrice(),
                    savedOrder.getCreatedAt()
                );

                exchange.getMessage().setHeader(
                    Exchange.HTTP_RESPONSE_CODE, 201);
                exchange.getMessage().setHeader(
                    Exchange.CONTENT_TYPE, "application/json");
                exchange.getMessage().setBody(response);
            })
            .marshal().json(JsonLibrary.Jackson);

        // GET: Fetch all orders

from("direct:get-all-orders")
    .routeId("get-all-orders-route")
    .log("GET ALL ORDERS route reached")
    .bean(orderService, "getAllOrders")
    .process(exchange -> {
        List<?> orders = exchange.getMessage().getBody(List.class);

        List<OrderResponse> responses = orders.stream()
            .map(item -> {
                Order order = (Order) item;

                return new OrderResponse(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getCustomerName(),
                    order.getProductName(),
                    order.getQuantity(),
                    order.getUnitPrice(),
                    order.getCreatedAt()
                );
            })
            .toList();

        exchange.getMessage().setBody(responses);
    })
    .marshal().json(JsonLibrary.Jackson);


        // GET: Fetch one order by ID
        from("direct:get-order-by-id")
            .routeId("get-order-by-id-route")
            .process(exchange -> {

                String id = exchange.getMessage()
                    .getHeader("id", String.class);

                try {
                    long orderId = Long.parseLong(id);

                    if (orderId <= 0) {
                        throw new IllegalArgumentException(
                            "Order ID must be a positive number");
                    }

                    exchange.getMessage().setBody(orderId);

                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                        "Order ID must be a valid positive number");
                }
            })
            .bean(orderService, "getOrderById")
            .process(exchange -> {

                Order order =
                    exchange.getMessage().getBody(Order.class);

                OrderResponse response = new OrderResponse(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getCustomerName(),
                    order.getProductName(),
                    order.getQuantity(),
                    order.getUnitPrice(),
                    order.getCreatedAt()
                );

                exchange.getMessage().setBody(response);
            })
            .marshal().json(JsonLibrary.Jackson);
    }
}
