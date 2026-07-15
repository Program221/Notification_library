package com.notifiaciones.lib.domain;

public record NotificationResult(boolean success,
                                 String messageId,
                                 String errorMessage,
                                 int attempts
) {


    public static NotificationResult ofSuccess(String messageId, int attempts) {
        return new NotificationResult(true, messageId, null, attempts);
    }

    public static NotificationResult ofFailure(String errorMessage, int attempts) {
        return new NotificationResult(false, null, errorMessage, attempts);
    }
}
