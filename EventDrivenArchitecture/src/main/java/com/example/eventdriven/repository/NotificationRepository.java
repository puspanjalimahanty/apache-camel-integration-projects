package com.example.eventdriven.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eventdriven.model.NotificationRecord;

public interface NotificationRepository extends JpaRepository<NotificationRecord, String> {
}