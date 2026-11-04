package com.example.sftprest.dto;

public class OrderResponse {

    private String message;
    private String orderId;
    private String status;

    public OrderResponse(String message, String orderId, String status) {
        this.message = message;
        this.orderId = orderId;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }
}