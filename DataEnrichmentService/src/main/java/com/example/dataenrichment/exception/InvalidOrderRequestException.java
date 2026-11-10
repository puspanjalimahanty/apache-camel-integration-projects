package com.example.dataenrichment.exception;

public class InvalidOrderRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidOrderRequestException(String message) {
        super(message);
    }
}
