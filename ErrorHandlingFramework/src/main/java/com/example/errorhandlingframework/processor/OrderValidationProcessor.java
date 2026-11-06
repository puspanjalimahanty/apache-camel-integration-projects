package com.example.errorhandlingframework.processor;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.errorhandlingframework.model.Order;
import com.example.errorhandlingframework.validator.OrderValidator;

@Component
public class OrderValidationProcessor implements Processor {

    private static final Logger log =
            LoggerFactory.getLogger(OrderValidationProcessor.class);

    private final OrderValidator orderValidator;

    public OrderValidationProcessor(OrderValidator orderValidator) {
        this.orderValidator = orderValidator;
    }

    @Override
    public void process(Exchange exchange) {

        @SuppressWarnings("unchecked")
        List<Order> orders =
                (List<Order>) exchange.getMessage().getBody();

        String correlationId =
                exchange.getMessage().getHeader("CorrelationId", String.class);

        for (Order order : orders) {

            orderValidator.validate(order);

            log.info(
                "Order validated | CorrelationId: {} | OrderId: {} | Customer: {} | Product: {} | Quantity: {} | UnitPrice: {}",
                correlationId,
                order.getOrderId(),
                order.getCustomerName(),
                order.getProductCode(),
                order.getQuantity(),
                order.getUnitPrice()
            );
        }

        exchange.getMessage().setBody(orders);
    }
}