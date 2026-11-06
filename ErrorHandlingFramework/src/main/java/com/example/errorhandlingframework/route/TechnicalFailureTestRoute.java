package com.example.errorhandlingframework.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.errorhandlingframework.processor.TechnicalFailureProcessor;

@Component
public class TechnicalFailureTestRoute extends RouteBuilder {

    private final TechnicalFailureProcessor technicalFailureProcessor;

    public TechnicalFailureTestRoute(
            TechnicalFailureProcessor technicalFailureProcessor) {
        this.technicalFailureProcessor = technicalFailureProcessor;
    }

    @Override
    public void configure() {

        from("file:{{app.file.input-directory}}"
                + "?move={{app.file.processed-directory}}"
                + "&moveFailed={{app.file.error-directory}}"
                + "&delay=1000"
                + "&initialDelay=0")
            .routeId("technical-failure-test-route")

            .log(LoggingLevel.INFO,
                "TEST ROUTE PICKED FILE: ${header.CamelFileName}")

            .convertBodyTo(String.class)

            .process(technicalFailureProcessor);
    }
}
