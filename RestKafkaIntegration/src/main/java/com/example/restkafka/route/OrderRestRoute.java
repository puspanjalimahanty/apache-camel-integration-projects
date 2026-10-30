
package com.example.restkafka.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.example.restkafka.dto.OrderRequest;

@Component
public class OrderRestRoute extends RouteBuilder {

    @Override
    public void configure() {

        restConfiguration()
            .component("platform-http")
            .bindingMode(RestBindingMode.json);

        rest("/api/orders")

            // Submit a new order
            .post()
                .type(OrderRequest.class)
                .to("direct:process-order")

            // Retrieve order processing status
            .get("/{orderId}/status")
                .outType(java.util.Map.class)
                .to("direct:get-order-status");
    }
}
