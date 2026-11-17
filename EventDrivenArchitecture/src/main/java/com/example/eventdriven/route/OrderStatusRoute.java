package com.example.eventdriven.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.eventdriven.service.OrderProcessingStatusService;

@Component
public class OrderStatusRoute extends RouteBuilder {

    private final OrderProcessingStatusService statusService;

    public OrderStatusRoute(OrderProcessingStatusService statusService) {
        this.statusService = statusService;
    }

    @Override
    public void configure() {

        rest("/api/orders")
            .get("/{orderId}/status")
            .to("direct:get-order-status");

        from("direct:get-order-status")
            .routeId("order-processing-status-route")

            .process(exchange -> {

                String orderId = exchange.getIn()
                        .getHeader("orderId", String.class);

                exchange.getMessage().setBody(
                        statusService.getOrderStatus(orderId)
                );
            });
    }
}