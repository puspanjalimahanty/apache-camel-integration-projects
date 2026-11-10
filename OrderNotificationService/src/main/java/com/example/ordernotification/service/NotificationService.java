package com.example.ordernotification.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.ordernotification.dto.OrderNotificationEvent;
import com.example.ordernotification.model.Notification;
import com.example.ordernotification.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification saveNotification(OrderNotificationEvent event) {

        Notification notification = new Notification();

        notification.setOrderId(event.getOrderId());
        notification.setCustomerName(event.getCustomerName());
        notification.setCustomerEmail(event.getCustomerEmail());
        notification.setEventType(event.getEventType());
        notification.setMessage(event.getMessage());
        notification.setStatus("PENDING");
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);

        System.out.println("Saved Notification ID: " + saved.getId());

        System.out.println("Total Notifications in Application DB: "
                + notificationRepository.count());

        return saved;
    }

    public void markAsSent(Notification notification) {

        notification.setStatus("SENT");
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
    
    public void markAsFailed(Notification notification) {

        notification.setStatus("FAILED");

        notificationRepository.save(notification);
    }
}
