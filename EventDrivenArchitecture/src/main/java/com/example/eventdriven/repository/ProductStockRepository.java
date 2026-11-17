package com.example.eventdriven.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.eventdriven.model.ProductStock;


public interface ProductStockRepository
        extends JpaRepository<ProductStock, String> {

    Optional<ProductStock> findByProductName(String productName);

    @Modifying
    @Query("""
        UPDATE ProductStock p
        SET p.availableQuantity = p.availableQuantity - :quantity
        WHERE p.productName = :productName
        AND p.availableQuantity >= :quantity
        """)
    int reserveStock(
        @Param("productName") String productName,
        @Param("quantity") int quantity
    );
}
