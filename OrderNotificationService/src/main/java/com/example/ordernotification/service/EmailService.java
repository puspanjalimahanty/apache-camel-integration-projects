package com.example.ordernotification.service;

import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.ordernotification.dto.OrderNotificationEvent;

@Service
public class EmailService {

    private final ProducerTemplate producerTemplate;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    public EmailService(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void sendOrderNotification(OrderNotificationEvent event) {

        String emailBody =
                "Hello " + event.getCustomerName() + ",\n\n"
                + event.getMessage() + "\n\n"
                + "Order ID: " + event.getOrderId()
                + "\n\nThank you for shopping with us!";

        Map<String, Object> headers = Map.of(
                "to", event.getCustomerEmail(),
                "from", username,
                "subject", "Order Notification - " + event.getOrderId()
        );

        String endpoint = "smtp://smtp.gmail.com:587"
                + "?username=" + username
                + "&password=" + password
                + "&mail.smtp.auth=true"
                + "&mail.smtp.starttls.enable=true";

        producerTemplate.sendBodyAndHeaders(
                endpoint, emailBody, headers);
    }
}