package com.example.eventdriven.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.eventdriven.dto.OrderFinalizedEvent;
import com.example.eventdriven.model.NotificationRecord;
import com.example.eventdriven.repository.NotificationRepository;
import com.example.eventdriven.service.FailedEventService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NotificationConsumerRoute extends RouteBuilder {

    private final NotificationRepository notificationRepository;
    private final FailedEventService failedEventService;
    private final ObjectMapper objectMapper;

    public NotificationConsumerRoute(
            NotificationRepository notificationRepository,
            FailedEventService failedEventService,
            ObjectMapper objectMapper) {

        this.notificationRepository = notificationRepository;
        this.failedEventService = failedEventService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure() {

        // Retry and DLQ handling
        onException(Exception.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(2000)
            .useOriginalMessage()
            .handled(true)
            .log(LoggingLevel.ERROR,
                "Notification processing failed after retries: ${exception.message}")
            .process(exchange -> {

                String originalMessage =
                    exchange.getIn().getBody(String.class);

                OrderFinalizedEvent event =
                    objectMapper.readValue(
                        originalMessage,
                        OrderFinalizedEvent.class
                    );

                Exception exception = exchange.getProperty(
                    Exchange.EXCEPTION_CAUGHT,
                    Exception.class
                );

                failedEventService.recordFailure(
                    event.getOrderId(),
                    "NOTIFICATION",
                    exception.getMessage()
                );
            })
            .to("kafka:notification-service-dlq?brokers=localhost:9092");


        // Consume finalized order events
        from("kafka:order-finalized-events"
                + "?brokers=localhost:9092"
                + "&groupId=notification-service")

            .routeId("notification-consumer-route")

            .unmarshal().json(
                org.apache.camel.model.dataformat.JsonLibrary.Jackson,
                OrderFinalizedEvent.class
            )

            // Check duplicate notification and validate event
            .process(exchange -> {

                OrderFinalizedEvent event =
                    exchange.getIn().getBody(OrderFinalizedEvent.class);

                if (notificationRepository.existsById(event.getOrderId())) {

                    log.info("Duplicate notification ignored for order: {}",
                            event.getOrderId());

                    exchange.setProperty("duplicateNotification", true);
                    return;
                }

                if (event.getCustomerEmail() == null
                        || event.getCustomerEmail().isBlank()) {

                    throw new IllegalArgumentException(
                        "Customer email is missing"
                    );
                }

                if (!"CONFIRMED".equals(event.getOrderStatus())
                        && !"FAILED".equals(event.getOrderStatus())) {

                    throw new IllegalArgumentException(
                        "Invalid final order status: " + event.getOrderStatus()
                    );
                }

                exchange.setProperty("orderEvent", event);
                exchange.setProperty("duplicateNotification", false);
            })

            // Process only new notifications
            .choice()

                .when(exchangeProperty("duplicateNotification").isEqualTo(true))
                    .log("Skipping duplicate notification")

                .otherwise()

                    // Prepare success or failure email
                    .process(exchange -> {

                        OrderFinalizedEvent event =
                            exchange.getProperty(
                                "orderEvent",
                                OrderFinalizedEvent.class
                            );

                        String subject;
                        String emailBody;

                        if ("CONFIRMED".equals(event.getOrderStatus())) {

                            subject = "Order Confirmation - " + event.getOrderId();

                            emailBody =
                                "Hello " + event.getCustomerName() + ",\n\n"
                                + "Your order has been successfully confirmed.\n\n"
                                + "Order Details:\n"
                                + "Order ID: " + event.getOrderId() + "\n"
                                + "Product: " + event.getProductName() + "\n"
                                + "Quantity: " + event.getQuantity() + "\n"
                                + "Total Amount: " + event.getTotalAmount() + "\n"
                                + "Order Status: CONFIRMED\n\n"
                                + "Thank you for shopping with us.";

                        } else {

                            subject = "Order Failed - " + event.getOrderId();

                            emailBody =
                                "Hello " + event.getCustomerName() + ",\n\n"
                                + "Unfortunately, your order could not be confirmed.\n\n"
                                + "Order Details:\n"
                                + "Order ID: " + event.getOrderId() + "\n"
                                + "Product: " + event.getProductName() + "\n"
                                + "Quantity: " + event.getQuantity() + "\n"
                                + "Total Amount: " + event.getTotalAmount() + "\n"
                                + "Order Status: FAILED\n\n"
                                + "Please contact our support team for assistance.";
                        }

                        exchange.getIn().setHeader(
                            "to", event.getCustomerEmail()
                        );

                        exchange.getIn().setHeader("subject", subject);

                        exchange.getIn().setHeader(
                            "Content-Type",
                            "text/plain; charset=UTF-8"
                        );

                        exchange.getIn().setBody(emailBody);
                    })

                    // Send email through SMTP
                    .to("smtp://{{mail.smtp.host}}:{{mail.smtp.port}}"
                        + "?username={{mail.smtp.username}}"
                        + "&password={{mail.smtp.password}}"
                        + "&mail.smtp.auth=true"
                        + "&mail.smtp.starttls.enable=true")

                    // Save notification after successful email delivery
                    .process(exchange -> {

                        OrderFinalizedEvent event =
                            exchange.getProperty(
                                "orderEvent",
                                OrderFinalizedEvent.class
                            );

                        NotificationRecord notification =
                            new NotificationRecord(
                                event.getOrderId(),
                                event.getCustomerName(),
                                "SENT"
                            );

                        notificationRepository.save(notification);

                        log.info(
                            "Final order notification sent to: {} for status: {}",
                            event.getCustomerEmail(),
                            event.getOrderStatus()
                        );
                    })

            .end();
    }
}
