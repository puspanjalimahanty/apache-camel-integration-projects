package com.example.restkafka.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restkafka.entity.OrderRecord;

public interface OrderRecordRepository
        extends JpaRepository<OrderRecord, Long> {

    Optional<OrderRecord> findByOrderId(String orderId);

    boolean existsByOrderId(String orderId);
}

