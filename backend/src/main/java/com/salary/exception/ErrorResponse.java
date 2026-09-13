package com.salary.exception;

import java.time.LocalDateTime;
import java.util.List;

public final class ErrorResponse {

    private final String message;
    private final LocalDateTime timestamp;
    private final List<String> fieldErrors;

    public ErrorResponse(String message) {
        this(message, null);
    }

    public ErrorResponse(String message, List<String> fieldErrors) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.fieldErrors = fieldErrors;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getFieldErrors() {
        return fieldErrors;
    }
}
