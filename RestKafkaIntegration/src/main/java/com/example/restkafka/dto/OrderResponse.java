package com.example.restkafka.dto;

public class OrderResponse {

    private String message;
    private String orderId;
    private String correlationId;
    private String status;

    public OrderResponse() {
    }

    public OrderResponse(String message, String orderId,
                         String correlationId, String status) {
        this.message = message;
        this.orderId = orderId;
        this.correlationId = correlationId;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
