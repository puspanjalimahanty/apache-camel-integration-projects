package com.example.kafkadb.controller;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafkadb.entity.OrderRecord;
import com.example.kafkadb.repository.OrderRecordRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRecordRepository repository;

    public OrderController(OrderRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<OrderRecord> getAllOrders() {
        return repository.findAll();
    }

    @GetMapping("/{orderId}")
    public OrderRecord getOrder(@PathVariable String orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Order not found: " + orderId));
    }
}
