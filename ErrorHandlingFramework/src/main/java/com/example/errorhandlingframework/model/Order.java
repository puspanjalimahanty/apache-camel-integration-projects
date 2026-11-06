package com.example.errorhandlingframework.model;

import java.math.BigDecimal;

public class Order {

    private String orderId;
    private String customerName;
    private String productCode;
    private Integer quantity;
    private BigDecimal unitPrice;

    public Order() {
    }

    public Order(String orderId, String customerName,
                 String productCode, Integer quantity,
                 BigDecimal unitPrice) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productCode = productCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
