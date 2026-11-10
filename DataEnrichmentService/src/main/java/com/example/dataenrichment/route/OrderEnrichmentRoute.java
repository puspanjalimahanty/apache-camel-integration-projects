package com.example.dataenrichment.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.example.dataenrichment.dto.ApiErrorResponse;
import com.example.dataenrichment.dto.EnrichedOrderResponse;
import com.example.dataenrichment.dto.OrderRequest;
import com.example.dataenrichment.exception.InvalidOrderRequestException;
import com.example.dataenrichment.exception.ResourceNotFoundException;
import com.example.dataenrichment.model.Customer;
import com.example.dataenrichment.model.Product;
import com.example.dataenrichment.processor.OrderEnrichmentStrategy;
import com.example.dataenrichment.processor.OrderValidationProcessor;
import com.example.dataenrichment.service.EnrichmentService;

@Component
public class OrderEnrichmentRoute extends RouteBuilder {

    private final EnrichmentService enrichmentService;
    private final OrderEnrichmentStrategy enrichmentStrategy;
    private final OrderValidationProcessor orderValidationProcessor;

    public OrderEnrichmentRoute(
            EnrichmentService enrichmentService,
            OrderEnrichmentStrategy enrichmentStrategy,
            OrderValidationProcessor orderValidationProcessor) {

        this.enrichmentService = enrichmentService;
        this.enrichmentStrategy = enrichmentStrategy;
        this.orderValidationProcessor = orderValidationProcessor;
    }

    @Override
    public void configure() {
    	

onException(ResourceNotFoundException.class)
    .handled(true)
    .process(exchange -> {

        ResourceNotFoundException exception =
                exchange.getProperty(
                    Exchange.EXCEPTION_CAUGHT,
                    ResourceNotFoundException.class);

        ApiErrorResponse error = new ApiErrorResponse(
                404,
                exception.getMessage());

        exchange.getIn().setBody(error);
        exchange.getIn().setHeader(
                Exchange.HTTP_RESPONSE_CODE, 404);
        exchange.getIn().setHeader(
                Exchange.CONTENT_TYPE, "application/json");
    });

onException(InvalidOrderRequestException.class)
.handled(true)
.process(exchange -> {

    InvalidOrderRequestException exception =
            exchange.getProperty(
                    Exchange.EXCEPTION_CAUGHT,
                    InvalidOrderRequestException.class);

    ApiErrorResponse error = new ApiErrorResponse(
            400,
            exception.getMessage());

    exchange.getIn().setBody(error);
    exchange.getIn().setHeader(
            Exchange.HTTP_RESPONSE_CODE, 400);
    exchange.getIn().setHeader(
            Exchange.CONTENT_TYPE, "application/json");
});

    

        // REST configuration
        restConfiguration()
                .component("platform-http")
                .bindingMode(RestBindingMode.json);

        // Incoming REST endpoint
        rest("/api/orders")
                .post("/enrich")
                .type(OrderRequest.class)
                .outType(EnrichedOrderResponse.class)
                .to("direct:enrich-order");

        // Main enrichment route
        from("direct:enrich-order")
                .routeId("order-enrichment-route")
                .process(orderValidationProcessor)

                // Retrieve customer details
                .process(exchange -> {
                    OrderRequest order = exchange.getIn()
                            .getBody(OrderRequest.class);

                    Customer customer = enrichmentService
                            .getCustomer(order.getCustomerId());

                    exchange.setProperty("customer", customer);
                })

                // Retrieve product and combine all details
                .enrich("direct:lookup-product", enrichmentStrategy);

        // Product lookup route
        from("direct:lookup-product")
                .routeId("product-lookup-route")

                .process(exchange -> {
                    OrderRequest order = exchange.getIn()
                            .getBody(OrderRequest.class);

                    Product product = enrichmentService
                            .getProduct(order.getProductCode());

                    exchange.getIn().setBody(product);
                });
    }
}

