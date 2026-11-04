package com.example.soaprest.route;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidationRoute extends RouteBuilder {

    @Override
    public void configure() {

        // Exception handler must be declared before routes
        onException(ValidationException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setBody(constant(
                Map.of(
                    "error", "Invalid customer ID",
                    "message",
                    "Customer ID must contain 3–20 letters, numbers, or hyphens"
                )
            ))
            .marshal().json(JsonLibrary.Jackson);

        // Customer request validation route
        from("direct:validate-customer-request")
            .routeId("customer-request-validation")

            .validate()
                .simple("${header.customerId} != null "
                    + "&& ${header.customerId} "
                    + "regex '^[A-Za-z0-9-]{3,20}$'")

            .to("direct:get-customer");
    }
}