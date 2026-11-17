package com.example.eventdriven.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "INVENTORY_RECORDS")
public class InventoryRecord {

    @Id
    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "QUANTITY")
    private int quantity;

    @Column(name = "INVENTORY_STATUS")
    private String inventoryStatus;

    public InventoryRecord() {
    }

    public InventoryRecord(String orderId, String productId,
                           String productName, int quantity,
                           String inventoryStatus) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.inventoryStatus = inventoryStatus;
    }

    // Backward-compatible constructor
    public InventoryRecord(String orderId, String productName,
                           int quantity, String inventoryStatus) {
        this(orderId, null, productName, quantity, inventoryStatus);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(String inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }
}
