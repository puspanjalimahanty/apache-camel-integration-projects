package com.example.eventdriven.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eventdriven.dto.OrderCreatedEvent;
import com.example.eventdriven.model.InventoryRecord;
import com.example.eventdriven.model.ProductStock;
import com.example.eventdriven.repository.InventoryRepository;
import com.example.eventdriven.repository.ProductStockRepository;

@Service
public class InventoryService {

    private final ProductStockRepository productStockRepository;
    private final InventoryRepository inventoryRepository;

    public InventoryService(
            ProductStockRepository productStockRepository,
            InventoryRepository inventoryRepository) {

        this.productStockRepository = productStockRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public String reserveInventory(OrderCreatedEvent event) {

        String orderId = event.getOrderId();
        String productName = event.getProductName();
        int quantity = event.getQuantity();

        // Prevent duplicate stock deduction
        if (inventoryRepository.existsById(orderId)) {
            return "DUPLICATE";
        }

        // Find product
        ProductStock product = productStockRepository
                .findByProductName(productName)
                .orElse(null);

        if (product == null) {

            saveInventoryRecord(
                orderId, null, productName, quantity,
                "PRODUCT_NOT_FOUND"
            );

            return "PRODUCT_NOT_FOUND";
        }

        // Validate quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Order quantity must be greater than zero"
            );
        }

        // Atomically reserve stock
        int updatedRows = productStockRepository.reserveStock(
            productName, quantity
        );

        if (updatedRows == 0) {

            saveInventoryRecord(
                orderId, product.getProductId(),
                productName, quantity, "OUT_OF_STOCK"
            );

            return "OUT_OF_STOCK";
        }

        // Save successful reservation
        saveInventoryRecord(
            orderId, product.getProductId(),
            productName, quantity, "RESERVED"
        );

        return "RESERVED";
    }

    private void saveInventoryRecord(
            String orderId,
            String productId,
            String productName,
            int quantity,
            String status) {

        InventoryRecord record = new InventoryRecord(
            orderId,
            productId,
            productName,
            quantity,
            status
        );

        inventoryRepository.save(record);
    }
}
