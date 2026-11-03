package com.example.filedb.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.filedb.dto.OrderFileRecord;
import com.example.filedb.dto.OrderResponse;
import com.example.filedb.entity.OrderRecord;
import com.example.filedb.repository.OrderRecordRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class OrderService {

    private final OrderRecordRepository repository;
    private final Validator validator;

    public OrderService(
            OrderRecordRepository repository,
            Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Transactional
    public void saveOrder(OrderFileRecord fileRecord, String sourceFile) {

        Set<ConstraintViolation<OrderFileRecord>> violations =
                validator.validate(fileRecord);

        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((first, second) -> first + "; " + second)
                    .orElse("Order validation failed");

            throw new IllegalArgumentException(errors);
        }

        if (repository.existsByOrderId(fileRecord.getOrderId())) {
            throw new IllegalArgumentException(
                    "Duplicate order ID: " + fileRecord.getOrderId());
        }

        OrderRecord order = new OrderRecord();
        order.setOrderId(fileRecord.getOrderId());
        order.setCustomerName(fileRecord.getCustomerName());
        order.setProductName(fileRecord.getProductName());
        order.setQuantity(fileRecord.getQuantity());
        order.setUnitPrice(fileRecord.getUnitPrice());
        order.setSourceFile(sourceFile);

        repository.save(order);
    }
    
    public List<OrderResponse> getAllOrders() {
        return repository.findAll()
                .stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getOrderId(),
                        order.getCustomerName(),
                        order.getProductName(),
                        order.getQuantity(),
                        order.getUnitPrice(),
                        order.getSourceFile(),
                        order.getReceivedAt()))
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderByOrderId(String orderId) {
        OrderRecord order = repository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId));

        return new OrderResponse(
                order.getId(),
                order.getOrderId(),
                order.getCustomerName(),
                order.getProductName(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getSourceFile(),
                order.getReceivedAt());
    }
    
    
    
    
}
