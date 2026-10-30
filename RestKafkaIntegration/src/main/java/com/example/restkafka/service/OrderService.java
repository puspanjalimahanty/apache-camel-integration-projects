package com.example.restkafka.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.restkafka.entity.OrderRecord;
import com.example.restkafka.repository.OrderRecordRepository;

@Service
public class OrderService {

    private final OrderRecordRepository orderRecordRepository;

    public OrderService(OrderRecordRepository orderRecordRepository) {
        this.orderRecordRepository = orderRecordRepository;
    }

    // Register a new order
    @Transactional
    public OrderRecord registerOrder(String orderId, String correlationId) {

        if (orderRecordRepository.existsByOrderId(orderId)) {
            throw new IllegalArgumentException(
                    "Order already exists: " + orderId);
        }

        OrderRecord record = new OrderRecord();
        record.setOrderId(orderId);
        record.setCorrelationId(correlationId);
        record.setStatus("PENDING");

        try {
            return orderRecordRepository.saveAndFlush(record);
        } catch (DataIntegrityViolationException ex) {
            // Handles concurrent requests for the same order ID.
            throw new IllegalArgumentException(
                    "Order already exists: " + orderId, ex);
        }
    }

    // Mark order as successfully published
    @Transactional
    public void markPublished(String orderId) {

        OrderRecord record = getOrderRecord(orderId);
        record.setStatus("PUBLISHED");
        record.setErrorMessage(null);

        orderRecordRepository.save(record);
    }

    // Mark order as failed
    @Transactional
    public void markFailed(String orderId, String errorMessage) {

        OrderRecord record = getOrderRecord(orderId);
        record.setStatus("FAILED");
        record.setErrorMessage(limitErrorMessage(errorMessage));

        orderRecordRepository.save(record);
    }

    // Retrieve an order record
    @Transactional(readOnly = true)
    public OrderRecord getOrderRecord(String orderId) {

        return orderRecordRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));
    }

    private String limitErrorMessage(String message) {

        if (message == null) {
            return "Unknown publishing error";
        }

        return message.length() > 1000
                ? message.substring(0, 1000)
                : message;
    }
}

