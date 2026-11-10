package com.example.dataenrichment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dataenrichment.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
}
