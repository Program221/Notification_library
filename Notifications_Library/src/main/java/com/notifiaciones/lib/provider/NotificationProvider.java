package com.notifiaciones.lib.provider;

import com.notifiaciones.lib.domain.Notification;
import com.notifiaciones.lib.domain.NotificationResult;
import com.notifiaciones.lib.exception.NotificationException;

public interface NotificationProvider {
    NotificationResult send(Notification notification) throws NotificationException;

    boolean supports(NotificationChannel channel);

    enum NotificationChannel {
        EMAIL, SMS, PUSH_NOTIFICATION
    }
}

