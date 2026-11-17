package com.example.eventdriven.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FailedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String serviceName;
    private String errorMessage;
    private String failureStatus;
    private LocalDateTime failedAt;

    public FailedEvent() {
    }

    public FailedEvent(String orderId, String serviceName,
                       String errorMessage, String failureStatus) {
        this.orderId = orderId;
        this.serviceName = serviceName;
        this.errorMessage = errorMessage;
        this.failureStatus = failureStatus;
        this.failedAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getFailureStatus() {
		return failureStatus;
	}

	public LocalDateTime getFailedAt() {
		return failedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setFailureStatus(String failureStatus) {
		this.failureStatus = failureStatus;
	}

	public void setFailedAt(LocalDateTime failedAt) {
		this.failedAt = failedAt;
	}

    
}
