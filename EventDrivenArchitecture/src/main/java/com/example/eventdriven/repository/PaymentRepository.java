package com.example.eventdriven.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eventdriven.model.PaymentRecord;

public interface PaymentRepository extends JpaRepository<PaymentRecord, String> {
}
