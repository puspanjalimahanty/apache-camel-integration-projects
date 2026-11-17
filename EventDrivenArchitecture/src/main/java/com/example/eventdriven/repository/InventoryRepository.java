package com.example.eventdriven.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eventdriven.model.InventoryRecord;

public interface InventoryRepository extends JpaRepository<InventoryRecord, String> {
}
