package com.example.dataenrichment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dataenrichment.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
