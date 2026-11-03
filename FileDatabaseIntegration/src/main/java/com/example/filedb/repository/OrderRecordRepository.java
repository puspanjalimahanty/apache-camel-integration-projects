package com.example.filedb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.filedb.entity.OrderRecord;

public interface OrderRecordRepository
        extends JpaRepository<OrderRecord, Long> {

    boolean existsByOrderId(String orderId);

    Optional<OrderRecord> findByOrderId(String orderId);
}