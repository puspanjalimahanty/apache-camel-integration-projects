package com.example.soaprest.route;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.stereotype.Component;

import com.example.soaprest.model.Customer;
import com.example.soaprest.soap.CustomerSoapResponse;

@Component
public class CustomerSoapRoute extends RouteBuilder {

    @Override
    public void configure() {
    	
    	onException(Exception.class)
        .maximumRedeliveries(3)
        .redeliveryDelay(2000)
        .retryAttemptedLogLevel(
            org.apache.camel.LoggingLevel.WARN
        )
        .handled(true)
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(502))
        .setBody(constant(
            java.util.Map.of(
                "error", "Customer service is temporarily unavailable"
            )
        ))
        .marshal().json(JsonLibrary.Jackson);

        from("direct:get-customer")
            .routeId("rest-to-soap-customer-route")
            .setHeader("X-Correlation-ID")
            .method(UUID.class, "randomUUID")

            // Prepare SOAP operation parameter
            .setBody(simple("${header.customerId}"))

            // Invoke SOAP service
            .to("cxf://{{customer.soap.endpoint}}"
                + "?serviceClass=com.example.soaprest.soap.CustomerSoapService"
                + "&wsdlURL=http://localhost:8086/services/customer?wsdl"
                + "&dataFormat=POJO")

            // Convert SOAP response to REST response object
            .process(exchange -> {

                Object response = exchange.getMessage().getBody();

                if (response instanceof MessageContentsList messageContentsList) {

                    Object soapResult = messageContentsList.get(0);

                    if (soapResult instanceof CustomerSoapResponse soapResponse) {

                        if ("Customer Not Found".equals(
                                soapResponse.getCustomerName())) {

                            exchange.getMessage().setHeader(
                                Exchange.HTTP_RESPONSE_CODE, 404
                            );

                            exchange.getMessage().setBody(
                                new java.util.HashMap<String, String>() {{
                                    put("error", "Customer not found");
                                    put("customerId",
                                        soapResponse.getCustomerId());
                                }}
                            );

                        } else {

                            Customer customer = new Customer(
                                soapResponse.getCustomerId(),
                                soapResponse.getCustomerName(),
                                soapResponse.getEmail()
                            );

                            exchange.getMessage().setBody(customer);
                        }

                    } else {
                        throw new IllegalStateException(
                            "Unexpected SOAP response type: "
                                + soapResult.getClass().getName()
                        );
                    }

                } else {
                    throw new IllegalStateException(
                        "Unexpected CXF response type: "
                            + response.getClass().getName()
                    );
                }
            })

            // Convert Java response object to JSON
            .marshal().json(JsonLibrary.Jackson)

            .log("Customer response transformed for REST: ${body}");
    }
}
