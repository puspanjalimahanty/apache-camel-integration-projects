package com.example.eventdriven.route;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.eventdriven.model.FailedEvent;
import com.example.eventdriven.service.FailedEventService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FailedEventRoute extends RouteBuilder {

    private final FailedEventService failedEventService;
    private final ObjectMapper objectMapper;

    public FailedEventRoute(
            FailedEventService failedEventService,
            ObjectMapper objectMapper) {

        this.failedEventService = failedEventService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure() {

        rest("/api/failed-events")
            .get()
            .produces("application/json")
            .to("direct:get-all-failed-events");

        from("direct:get-all-failed-events")
            .routeId("get-all-failed-events-route")
            .process(exchange -> {

                List<FailedEvent> failures =
                        failedEventService.getAllFailures();

                String jsonResponse =
                        objectMapper.writeValueAsString(failures);

                exchange.getMessage().setHeader(
                        Exchange.CONTENT_TYPE,
                        "application/json"
                );

                exchange.getMessage().setBody(jsonResponse);
            });
    }
}
