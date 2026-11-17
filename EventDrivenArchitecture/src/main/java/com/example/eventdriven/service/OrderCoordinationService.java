package com.example.eventdriven.service;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eventdriven.dto.OrderFinalizedEvent;
import com.example.eventdriven.model.OrderRecord;
import com.example.eventdriven.repository.OrderRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderCoordinationService {

    private final OrderRecordRepository orderRecordRepository;
    private final ProducerTemplate producerTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log =
            LoggerFactory.getLogger(OrderCoordinationService.class);

    public OrderCoordinationService(
            OrderRecordRepository orderRecordRepository,
            ProducerTemplate producerTemplate,
            ObjectMapper objectMapper) {
        this.orderRecordRepository = orderRecordRepository;
        this.producerTemplate = producerTemplate;
        this.objectMapper=objectMapper;
    }

    @Transactional
    public void updatePaymentStatus(String orderId, String paymentStatus) {
        OrderRecord record = getOrderRecord(orderId);

        record.setPaymentStatus(paymentStatus);
        updateFinalOrderStatus(record);
    }

    @Transactional
    public void updateInventoryStatus(String orderId, String inventoryStatus) {
        OrderRecord record = getOrderRecord(orderId);

        record.setInventoryStatus(inventoryStatus);
        updateFinalOrderStatus(record);
    }

    private OrderRecord getOrderRecord(String orderId) {
        return orderRecordRepository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalStateException(
                        "Order record not found: " + orderId));
    }


private void updateFinalOrderStatus(OrderRecord record) {

    String payment = record.getPaymentStatus();
    String inventory = record.getInventoryStatus();

    // Wait until both services complete processing
    if ("PENDING".equals(payment) || "PENDING".equals(inventory)) {
        record.setOrderStatus("PENDING");
        return;
    }

    // Publish only when the order is finalized for the first time
    if (!"PENDING".equals(record.getOrderStatus())) {
        return;
    }

    if ("SUCCESS".equals(payment) && "RESERVED".equals(inventory)) {
        record.setOrderStatus("CONFIRMED");
    } else {
        record.setOrderStatus("FAILED");
    }

    orderRecordRepository.save(record);

    publishFinalizedEvent(record);

    log.info(
        "Order finalized. Order ID: {}, Payment: {}, Inventory: {}, Final Status: {}",
        record.getOrderId(),
        payment,
        inventory,
        record.getOrderStatus()
    );
}




private void publishFinalizedEvent(OrderRecord record) {

    OrderFinalizedEvent event = new OrderFinalizedEvent(
            record.getOrderId(),
            record.getCustomerName(),
            record.getCustomerEmail(),
            record.getProductName(),
            record.getQuantity(),
            record.getTotalAmount(),
            record.getOrderStatus()
    );

    try {
        String jsonMessage = objectMapper.writeValueAsString(event);

        producerTemplate.sendBody(
                "kafka:order-finalized-events?brokers=localhost:9092",
                jsonMessage
        );

        log.info("Finalized order event published for: {}",
                record.getOrderId());

    } catch (JsonProcessingException e) {
        throw new IllegalStateException(
                "Failed to convert finalized order event into JSON", e);
    }
}


}
