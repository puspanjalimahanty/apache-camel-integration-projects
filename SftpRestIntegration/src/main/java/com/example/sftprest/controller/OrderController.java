package com.example.sftprest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sftprest.dto.OrderRequest;
import com.example.sftprest.dto.OrderResponse;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponse> receiveOrder(
            @RequestBody OrderRequest order,
            @RequestHeader(value = "X-Correlation-ID", required = false)
            String correlationId) {

        System.out.println(
                "[" + correlationId + "] REST API received order: "
                + order.getOrderId()
        );

        OrderResponse response = new OrderResponse(
                "Order received successfully",
                order.getOrderId(),
                "RECEIVED"
        );

        return ResponseEntity.ok(response);
    }
}