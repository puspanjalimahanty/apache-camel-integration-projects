package com.example.kafkadb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kafkadb.entity.OrderRecord;

public interface OrderRecordRepository
        extends JpaRepository<OrderRecord, Long> {

    boolean existsByOrderId(String orderId);

    Optional<OrderRecord> findByOrderId(String orderId);
}
