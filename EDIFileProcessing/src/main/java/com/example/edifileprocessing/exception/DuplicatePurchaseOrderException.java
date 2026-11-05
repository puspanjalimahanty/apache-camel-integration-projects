package com.example.edifileprocessing.exception;

public class DuplicatePurchaseOrderException extends RuntimeException {

    public DuplicatePurchaseOrderException(String message) {
        super(message);
    }
}