package com.example.soaprest.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class CustomerRestRoute extends RouteBuilder {

    @Override
    public void configure() {

        restConfiguration()
            .component("platform-http")
            .port(8086)
            .bindingMode(RestBindingMode.json);

        rest("/api/customers")
            .get("/{customerId}")
            .to("direct:validate-customer-request");
    }
}
