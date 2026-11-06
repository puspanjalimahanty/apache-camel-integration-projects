package com.example.errorhandlingframework.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.errorhandlingframework.exception.InvalidOrderException;
import com.example.errorhandlingframework.model.Order;

@Component
public class OrderValidator {

    public void validate(Order order) {

        if (order.getOrderId() == null
                || order.getOrderId().isBlank()) {
            throw new InvalidOrderException("Order ID is missing");
        }

        if (order.getCustomerName() == null
                || order.getCustomerName().isBlank()) {
            throw new InvalidOrderException(
                    "Customer name is missing for order: "
                            + order.getOrderId());
        }

        if (order.getProductCode() == null
                || order.getProductCode().isBlank()) {
            throw new InvalidOrderException(
                    "Product code is missing for order: "
                            + order.getOrderId());
        }

        if (order.getQuantity() == null
                || order.getQuantity() <= 0) {
            throw new InvalidOrderException(
                    "Quantity must be greater than zero for order: "
                            + order.getOrderId());
        }

        if (order.getUnitPrice() == null
                || order.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderException(
                    "Unit price cannot be negative or missing for order: "
                            + order.getOrderId());
        }
    }
}
