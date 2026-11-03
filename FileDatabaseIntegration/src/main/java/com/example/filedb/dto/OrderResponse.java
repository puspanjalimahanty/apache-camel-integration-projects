package com.example.filedb.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    private Long id;
    private String orderId;
    private String customerName;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String sourceFile;
    private LocalDateTime receivedAt;

    public OrderResponse(
            Long id,
            String orderId,
            String customerName,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            String sourceFile,
            LocalDateTime receivedAt) {

        this.id = id;
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.sourceFile = sourceFile;
        this.receivedAt = receivedAt;
    }

    public Long getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getSourceFile() { return sourceFile; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
}