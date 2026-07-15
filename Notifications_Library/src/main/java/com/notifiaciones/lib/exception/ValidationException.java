package com.notifiaciones.lib.exception;

public class ValidationException extends NotificationException {
    public ValidationException(String message) {
        super(message);
    }

    @Override
    public String getErrorType() {
        return "VALIDATION_ERROR";
    }
}

