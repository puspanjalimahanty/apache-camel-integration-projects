package com.example.errorhandlingframework.processor;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class CorrelationIdProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        String correlationId = UUID.randomUUID().toString();

        exchange.getMessage().setHeader("CorrelationId", correlationId);
    }
}