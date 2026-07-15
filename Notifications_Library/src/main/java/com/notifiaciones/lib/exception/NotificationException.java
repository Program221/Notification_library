package com.notifiaciones.lib.exception;

public class NotificationException extends RuntimeException {
    public NotificationException(String message) {
        super(message);
    }

    public String getErrorType() {
        return "DELIVERY_ERROR";
    }
}
