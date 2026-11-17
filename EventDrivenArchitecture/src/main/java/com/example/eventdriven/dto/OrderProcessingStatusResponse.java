package com.example.eventdriven.dto;

public class OrderProcessingStatusResponse {

    private String orderId;
    private String paymentStatus;
    private String inventoryStatus;
    private String notificationStatus;
    private String orderStatus;

    public OrderProcessingStatusResponse() {
    }

    public OrderProcessingStatusResponse(
            String orderId,
            String paymentStatus,
            String inventoryStatus,
            String notificationStatus,
            String orderStatus) {

        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.inventoryStatus = inventoryStatus;
        this.notificationStatus = notificationStatus;
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(String inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
