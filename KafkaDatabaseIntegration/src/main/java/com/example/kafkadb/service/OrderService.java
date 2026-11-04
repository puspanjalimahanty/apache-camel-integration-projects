package com.example.kafkadb.service;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kafkadb.dto.OrderMessage;
import com.example.kafkadb.entity.OrderRecord;
import com.example.kafkadb.repository.OrderRecordRepository;

import jakarta.validation.Validator;

@Service
public class OrderService {

    private final OrderRecordRepository repository;
    private final Validator validator;
    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRecordRepository repository,
                        Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Transactional
    public void saveOrder(OrderMessage message) {

        // Validate incoming order
    	// Validate incoming order
    	var violations = validator.validate(message);

    	if (!violations.isEmpty()) {

    	    String errors = violations.stream()
    	            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
    	            .reduce((a, b) -> a + ", " + b)
    	            .orElse("Invalid order");

    	    log.error("Order validation failed for orderId {}: {}",
    	            message.getOrderId(), errors);

    	    throw new IllegalArgumentException(
    	            "Order validation failed: " + errors);
    	}
        // Check duplicate order
    	// Check duplicate order
    	if (repository.existsByOrderId(message.getOrderId())) {

    	    log.warn("Duplicate order received. Skipping orderId: {}",
    	            message.getOrderId());

    	    return;
    	}
        // Convert DTO to Entity
        OrderRecord record = new OrderRecord();

        record.setOrderId(message.getOrderId());
        record.setCustomerName(message.getCustomerName());
        record.setProductName(message.getProductName());
        record.setQuantity(message.getQuantity());
        record.setUnitPrice(message.getUnitPrice());
        record.setCorrelationId(message.getCorrelationId());

        // Save into database
        repository.save(record);
    }
}
