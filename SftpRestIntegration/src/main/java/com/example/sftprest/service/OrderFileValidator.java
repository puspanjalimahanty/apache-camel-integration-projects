package com.example.sftprest.service;

import org.springframework.stereotype.Service;

@Service
public class OrderFileValidator {

    public void validate(String content) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Order file is empty");
        }

        String[] fields = content.trim().split(",");

        if (fields.length != 5) {
            throw new IllegalArgumentException(
                    "Invalid order format. Expected 5 fields");
        }

        if (fields[0].trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (fields[1].trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }

        if (fields[2].trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }

        try {
            int quantity = Integer.parseInt(fields[3].trim());

            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero");
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity must be a valid number");
        }

        try {
            double unitPrice = Double.parseDouble(fields[4].trim());

            if (unitPrice < 0) {
                throw new IllegalArgumentException(
                        "Unit price cannot be negative");
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unit price must be a valid number");
        }
    }
}