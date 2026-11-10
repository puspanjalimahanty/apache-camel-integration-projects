package com.example.ordernotification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ordernotification.model.Notification;


public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

}
