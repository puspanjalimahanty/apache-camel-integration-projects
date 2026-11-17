package com.example.eventdriven.service;

import org.springframework.stereotype.Service;

import com.example.eventdriven.dto.OrderProcessingStatusResponse;
import com.example.eventdriven.repository.InventoryRepository;
import com.example.eventdriven.repository.NotificationRepository;
import com.example.eventdriven.repository.OrderRecordRepository;
import com.example.eventdriven.repository.PaymentRepository;

@Service
public class OrderProcessingStatusService {

    private final PaymentRepository paymentRepository;
    private final InventoryRepository inventoryRepository;
    private final NotificationRepository notificationRepository;
    private final OrderRecordRepository orderRecordRepository;

    public OrderProcessingStatusService(
            PaymentRepository paymentRepository,
            InventoryRepository inventoryRepository,
            NotificationRepository notificationRepository,
            OrderRecordRepository orderRecordRepository) {

        this.paymentRepository = paymentRepository;
        this.inventoryRepository = inventoryRepository;
        this.notificationRepository = notificationRepository;
        this.orderRecordRepository = orderRecordRepository;
    }

    public OrderProcessingStatusResponse getOrderStatus(String orderId) {

        String paymentStatus = paymentRepository.findById(orderId)
                .map(record -> record.getPaymentStatus())
                .orElse("PENDING");

        String inventoryStatus = inventoryRepository.findById(orderId)
                .map(record -> record.getInventoryStatus())
                .orElse("PENDING");

        String notificationStatus = notificationRepository.findById(orderId)
                .map(record -> record.getNotificationStatus())
                .orElse("PENDING");

        String orderStatus = orderRecordRepository.findById(orderId)
                .map(record -> record.getOrderStatus())
                .orElse("NOT_FOUND");

        return new OrderProcessingStatusResponse(
                orderId,
                paymentStatus,
                inventoryStatus,
                notificationStatus,
                orderStatus
        );
    }
}
