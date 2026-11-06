package com.example.errorhandlingframework.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.errorhandlingframework.processor.CorrelationIdProcessor;
import com.example.errorhandlingframework.processor.FileContentProcessor;
import com.example.errorhandlingframework.processor.OrderValidationProcessor;

@Component
public class OrderFileRoute extends RouteBuilder {

    private final CorrelationIdProcessor correlationIdProcessor;
    private final FileContentProcessor fileContentProcessor;
    private final OrderValidationProcessor orderValidationProcessor;

    public OrderFileRoute(
            CorrelationIdProcessor correlationIdProcessor,
            FileContentProcessor fileContentProcessor,
            OrderValidationProcessor orderValidationProcessor) {

        this.correlationIdProcessor = correlationIdProcessor;
        this.fileContentProcessor = fileContentProcessor;
        this.orderValidationProcessor = orderValidationProcessor;
    }

    @Override
    public void configure() {

        from("file:{{app.file.input-directory}}"
                + "?move={{app.file.processed-directory}}"
                + "&moveFailed={{app.file.error-directory}}")
            .routeId("order-file-processing-route")

            // Generate correlation ID
            .process(correlationIdProcessor)

            .log(LoggingLevel.INFO,
                "Received file: ${header.CamelFileName}, "
                + "Correlation ID: ${header.CorrelationId}")

            // Parse CSV content into Order objects
            .process(fileContentProcessor)

            // Validate order data
            .process(orderValidationProcessor) 

            .log(LoggingLevel.INFO,
                "File parsing and validation successful. "
                + "Correlation ID: ${header.CorrelationId}, " 
                + "Order count: ${body.size()}");
    }
}