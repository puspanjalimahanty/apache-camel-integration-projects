package com.example.orderrestdatabase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orderrestdatabase.entity.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByOrderNumber(String orderNumber);
}