package com.example.eventdriven.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumerRoute extends RouteBuilder {

    @Override
    public void configure() {
        // Payment processing is now handled through PaymentApiRoute.
        // No automatic payment consumption from order-created-events.
    }
}
