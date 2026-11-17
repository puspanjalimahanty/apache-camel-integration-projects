package com.example.eventdriven.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.eventdriven.dto.PaymentRequest;
import com.example.eventdriven.model.OrderRecord;
import com.example.eventdriven.model.PaymentRecord;
import com.example.eventdriven.repository.OrderRecordRepository;
import com.example.eventdriven.repository.PaymentRepository;

@Service
public class PaymentService {

    private final OrderRecordRepository orderRecordRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            OrderRecordRepository orderRecordRepository,
            PaymentRepository paymentRepository) {

        this.orderRecordRepository = orderRecordRepository;
        this.paymentRepository = paymentRepository;
    }

    public String processPayment(PaymentRequest request) {

        OrderRecord order = orderRecordRepository
            .findById(request.getOrderId())
            .orElse(null);

        if (order == null) {
            return "ORDER_NOT_FOUND";
        }

        if (paymentRepository.existsById(request.getOrderId())) {
            return "DUPLICATE_PAYMENT";
        }

        BigDecimal expectedAmount =
            BigDecimal.valueOf(order.getTotalAmount());

        BigDecimal receivedAmount =
            BigDecimal.valueOf(request.getAmount());

        String paymentStatus;

        if (receivedAmount.compareTo(expectedAmount) < 0) {
            paymentStatus = "INSUFFICIENT_AMOUNT";
        } else if (receivedAmount.compareTo(expectedAmount) > 0) {
            paymentStatus = "AMOUNT_MISMATCH";
        } else {
            paymentStatus = "SUCCESS";
        }

        PaymentRecord paymentRecord = new PaymentRecord(
            request.getOrderId(),
            request.getAmount(),
            paymentStatus
        );

        paymentRepository.save(paymentRecord);

        return paymentStatus;
    }
}
