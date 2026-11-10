package com.example.ordernotification.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.example.ordernotification.dto.OrderNotificationEvent;
import com.example.ordernotification.model.Notification;
import com.example.ordernotification.service.EmailService;
import com.example.ordernotification.service.NotificationService;

@Component
public class OrderNotificationRoute extends RouteBuilder {

    private final NotificationService notificationService;
    private final EmailService emailService;

    public OrderNotificationRoute(
            NotificationService notificationService,
            EmailService emailService) {

        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    public void configure() {

        onException(Exception.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(2000)
            .useExponentialBackOff()
            .backOffMultiplier(2)
            .retryAttemptedLogLevel(LoggingLevel.WARN)
            .log("Notification processing failed. Retry attempt: ${header.CamelRedeliveryCounter}")
            .log("Moving failed notification to Dead Letter Queue: ${exception.message}")
            .process(exchange -> {

                Notification notification =
                        exchange.getProperty("notification", Notification.class);

                if (notification != null) {
                    notificationService.markAsFailed(notification);
                }
            })
            .handled(true)
            .marshal().json(JsonLibrary.Jackson)
            .to("file:dead-letter?fileName=notification-${date:now:yyyyMMdd-HHmmssSSS}.json");

        from("kafka:order-events?brokers={{camel.component.kafka.brokers}}&groupId=notification-service")
            .routeId("order-notification-consumer-route")
            .log("Received order event: ${body}")
            .unmarshal()
            .json(JsonLibrary.Jackson, OrderNotificationEvent.class)
         // Step 1: Save notification only once
            .process(exchange -> {

                OrderNotificationEvent event =
                        exchange.getMessage().getBody(OrderNotificationEvent.class);

                Notification notification =
                        notificationService.saveNotification(event);

                exchange.setProperty("notification", notification);
            })

            // Step 2: Send email (this processor will be retried on failure)
            .process(exchange -> {

                OrderNotificationEvent event =
                        exchange.getMessage().getBody(OrderNotificationEvent.class);

                emailService.sendOrderNotification(event);
            })

            // Step 3: Update status after successful email delivery
            .process(exchange -> {

                Notification notification =
                        exchange.getProperty("notification", Notification.class);

                notificationService.markAsSent(notification);
            })

            .log("Order notification email sent successfully");
    }
}
