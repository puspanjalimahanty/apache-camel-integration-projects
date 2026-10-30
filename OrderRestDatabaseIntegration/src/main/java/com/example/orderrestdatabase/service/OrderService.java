package com.example.orderrestdatabase.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orderrestdatabase.entity.Order;
import com.example.orderrestdatabase.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        System.out.println("Total orders fetched from H2: " + orders.size());

        for (Order order : orders) {
            printOrderDetails(order);
        }

        return orders;
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                "Order ID must be a positive number");
        }

        Order order = orderRepository.findById(id)
            .orElseThrow(() ->
                new OrderNotFoundException(
                    "Order not found with id: " + id));

        System.out.println("Single order fetched from H2:");
        printOrderDetails(order);

        return order;
    }

    @Transactional
    public Order createOrder(Order order) {
        String orderNumber = order.getOrderNumber();

        if (orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException(
                "Order number is required");
        }

        orderNumber = orderNumber.trim();
        order.setOrderNumber(orderNumber);

        if (orderRepository.existsByOrderNumber(orderNumber)) {
            throw new DuplicateOrderException(
                "Order already exists: " + orderNumber);
        }

        return orderRepository.saveAndFlush(order);
    }

    private void printOrderDetails(Order order) {
        System.out.println("ID: " + order.getId());
        System.out.println("Order Number: " + order.getOrderNumber());
        System.out.println("Customer Name: " + order.getCustomerName());
        System.out.println("Product Name: " + order.getProductName());
        System.out.println("Quantity: " + order.getQuantity());
        System.out.println("Unit Price: " + order.getUnitPrice());
        System.out.println("Created At: " + order.getCreatedAt());
        System.out.println("------------------------------");
    }
}
