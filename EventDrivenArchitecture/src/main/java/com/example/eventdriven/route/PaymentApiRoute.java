package com.example.eventdriven.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.eventdriven.dto.PaymentRequest;
import com.example.eventdriven.service.OrderCoordinationService;
import com.example.eventdriven.service.PaymentService;

@Component
public class PaymentApiRoute extends RouteBuilder {

    private final PaymentService paymentService;
    private final OrderCoordinationService orderCoordinationService;

    public PaymentApiRoute(
            PaymentService paymentService,
            OrderCoordinationService orderCoordinationService) {

        this.paymentService = paymentService;
        this.orderCoordinationService = orderCoordinationService;
    }

    @Override
    public void configure() {

        rest("/api/payments")
            .post()
            .type(PaymentRequest.class)
            .to("direct:submit-payment");

        from("direct:submit-payment")
            .routeId("payment-api-route")
            .process(exchange -> {

                PaymentRequest request =
                    exchange.getIn().getBody(PaymentRequest.class);

                if (request == null
                        || request.getOrderId() == null
                        || request.getOrderId().isBlank()
                        || !Double.isFinite(request.getAmount())
                        || request.getAmount() <= 0) {

                    throw new IllegalArgumentException(
                        "Invalid payment request");
                }

                String paymentStatus =
                    paymentService.processPayment(request);

                log.info(
                    "Payment processed: orderId={}, amount={}, status={}",
                    request.getOrderId(),
                    request.getAmount(),
                    paymentStatus
                );

                // Send the payment result to Order Coordination.
                if (!paymentStatus.equals("ORDER_NOT_FOUND")
                        && !paymentStatus.equals("DUPLICATE_PAYMENT")) {

                    orderCoordinationService.updatePaymentStatus(
                        request.getOrderId(),
                        paymentStatus
                    );
                }

                exchange.getMessage().setHeader(
                    Exchange.CONTENT_TYPE,
                    "text/plain"
                );

                exchange.getMessage().setBody(
                    "Payment status: " + paymentStatus
                );
            });
    }
}
