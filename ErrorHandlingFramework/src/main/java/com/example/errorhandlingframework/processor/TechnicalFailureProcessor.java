package com.example.errorhandlingframework.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.example.errorhandlingframework.exception.TechnicalException;

@Component
public class TechnicalFailureProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        String orderId = exchange.getMessage()
                .getBody(String.class);

        throw new TechnicalException(
                "Simulated external system failure for order: "
                        + orderId,
                new RuntimeException("Simulated service unavailable")
        );
    }
}
