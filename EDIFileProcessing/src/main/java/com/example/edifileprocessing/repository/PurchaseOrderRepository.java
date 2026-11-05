package com.example.edifileprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.edifileprocessing.entity.PurchaseOrderEntity;

public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrderEntity, String> {
}