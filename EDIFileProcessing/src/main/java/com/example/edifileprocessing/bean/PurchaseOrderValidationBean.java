package com.example.edifileprocessing.bean;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.edifileprocessing.model.PurchaseOrder;

@Component
public class PurchaseOrderValidationBean {

    public void validate(PurchaseOrder order) {

        if (order.getOrderId() == null || order.getOrderId().isBlank()) {
            throw new IllegalArgumentException(
                    "Purchase order number is missing");
        }

        if (order.getBuyerName() == null || order.getBuyerName().isBlank()) {
            throw new IllegalArgumentException(
                    "Buyer name is missing");
        }

        if (order.getProductCode() == null || order.getProductCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Product code is missing");
        }

        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        if (order.getUnitPrice() == null
                || order.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Unit price must be greater than zero");
        }
    }
}