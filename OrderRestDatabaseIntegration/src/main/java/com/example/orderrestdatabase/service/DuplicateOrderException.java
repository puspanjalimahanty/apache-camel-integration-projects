package com.example.orderrestdatabase.service;

public class DuplicateOrderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateOrderException(String message) {
        super(message);
    }
}