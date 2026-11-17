package com.example.eventdriven.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eventdriven.model.FailedEvent;

public interface FailedEventRepository
        extends JpaRepository<FailedEvent, Long> {
}
