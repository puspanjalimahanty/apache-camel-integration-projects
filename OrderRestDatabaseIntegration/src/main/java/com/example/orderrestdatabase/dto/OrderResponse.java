package com.example.orderrestdatabase.dto;

import java.math.BigDecimal;

public class OrderResponse {

    private Long id;
    private String orderNumber;
    private String customerName;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String createdAt;

    public OrderResponse(
            Long id,
            String orderNumber,
            String customerName,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            java.time.LocalDateTime createdAt) {

        this.id = id;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt != null ? createdAt.toString() : null;
    }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getCustomerName() { return customerName; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getCreatedAt() { return createdAt; }
}