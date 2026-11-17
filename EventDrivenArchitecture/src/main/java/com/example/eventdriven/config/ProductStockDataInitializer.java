package com.example.eventdriven.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.eventdriven.model.ProductStock;
import com.example.eventdriven.repository.ProductStockRepository;

@Component
public class ProductStockDataInitializer implements CommandLineRunner {

    private final ProductStockRepository productStockRepository;

    public ProductStockDataInitializer(
            ProductStockRepository productStockRepository) {
        this.productStockRepository = productStockRepository;
    }

    @Override
    public void run(String... args) {

        if (!productStockRepository.existsById("P101")) {
            ProductStock laptop = new ProductStock("P101", "Laptop", 10);
            laptop.setUnitPrice(new BigDecimal("50000.00"));
            productStockRepository.save(laptop);
        }

        if (!productStockRepository.existsById("P102")) {
            ProductStock mobile = new ProductStock("P102", "Mobile", 5);
            mobile.setUnitPrice(new BigDecimal("25000.00"));
            productStockRepository.save(mobile);
        }

        if (!productStockRepository.existsById("P103")) {
            ProductStock headphones = new ProductStock("P103", "Headphones", 3);
            headphones.setUnitPrice(new BigDecimal("2000.00"));
            productStockRepository.save(headphones);
        }

        System.out.println("Initial product stock and prices loaded successfully.");
    }
}
