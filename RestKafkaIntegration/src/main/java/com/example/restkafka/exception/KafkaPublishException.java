package com.example.restkafka.exception;

public class KafkaPublishException extends RuntimeException {

    public KafkaPublishException(String message) {
        super(message);
    }
}

