package com.example.dataenrichment.service;

import org.springframework.stereotype.Service;

import com.example.dataenrichment.exception.ResourceNotFoundException;
import com.example.dataenrichment.model.Customer;
import com.example.dataenrichment.model.Product;
import com.example.dataenrichment.repository.CustomerRepository;
import com.example.dataenrichment.repository.ProductRepository;

@Service
public class EnrichmentService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public EnrichmentService(
            CustomerRepository customerRepository,
            ProductRepository productRepository) {

        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public Customer getCustomer(String customerId) {

        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: " + customerId));
    }

    public Product getProduct(String productCode) {

        return productRepository.findById(productCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + productCode));
    }
}
