package com.example.orderrestdatabase.dto;




public class ApiErrorResponse {

    private final String timestamp;
    private final int status;
    private final String error;
    private final String message;

    public ApiErrorResponse(
            int status, String error, String message) {
    	this.timestamp = java.time.Instant.now().toString();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}

