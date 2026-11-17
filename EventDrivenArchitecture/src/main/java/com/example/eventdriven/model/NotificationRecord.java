package com.example.eventdriven.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_records")
public class NotificationRecord {

    @Id
    private String orderId;

    private String customerName;

    private String notificationStatus;

    public NotificationRecord() {
    }

    public NotificationRecord(String orderId, String customerName,
            String notificationStatus) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.notificationStatus = notificationStatus;
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

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}