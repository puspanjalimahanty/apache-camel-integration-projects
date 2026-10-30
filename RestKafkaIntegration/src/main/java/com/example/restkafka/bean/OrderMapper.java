package com.example.restkafka.bean;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.restkafka.dto.OrderRequest;

@Component
public class OrderMapper {

    public Map<String, Object> toKafkaMessage(
            OrderRequest order, String correlationId) {

        Map<String, Object> message = new LinkedHashMap<>();

        message.put("orderId", order.getOrderId());
        message.put("customerName", order.getCustomerName());
        message.put("productName", order.getProductName());
        message.put("quantity", order.getQuantity());
        message.put("unitPrice", order.getUnitPrice());
        message.put("correlationId", correlationId);
        message.put("submittedAt", LocalDateTime.now().toString());

        return message;
    }
}
