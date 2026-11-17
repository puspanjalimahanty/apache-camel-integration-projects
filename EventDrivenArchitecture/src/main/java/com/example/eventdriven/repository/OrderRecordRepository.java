package com.example.eventdriven.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.eventdriven.model.OrderRecord;

import jakarta.persistence.LockModeType;

@Repository
public interface OrderRecordRepository
        extends JpaRepository<OrderRecord, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrderRecord o WHERE o.orderId = :orderId")
    Optional<OrderRecord> findByOrderIdForUpdate(
            @Param("orderId") String orderId);
}
