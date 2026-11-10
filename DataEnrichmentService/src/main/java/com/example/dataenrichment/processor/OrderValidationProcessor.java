package com.example.dataenrichment.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.example.dataenrichment.dto.OrderRequest;
import com.example.dataenrichment.exception.InvalidOrderRequestException;

@Component
public class OrderValidationProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        OrderRequest order = exchange.getIn()
                .getBody(OrderRequest.class);

        if (order == null) {
            throw new InvalidOrderRequestException(
                    "Order request cannot be empty");
        }

        if (order.getOrderId() == null ||
                order.getOrderId().isBlank()) {
            throw new InvalidOrderRequestException(
                    "Order ID is required");
        }

        if (order.getCustomerId() == null ||
                order.getCustomerId().isBlank()) {
            throw new InvalidOrderRequestException(
                    "Customer ID is required");
        }

        if (order.getProductCode() == null ||
                order.getProductCode().isBlank()) {
            throw new InvalidOrderRequestException(
                    "Product code is required");
        }

        if (order.getQuantity() <= 0) {
            throw new InvalidOrderRequestException(
                    "Quantity must be greater than zero");
        }
    }
}
