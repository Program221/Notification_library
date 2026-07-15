package com.notifiaciones.lib.service;

import com.notifiaciones.lib.domain.Notification;
import com.notifiaciones.lib.domain.NotificationResult;
import com.notifiaciones.lib.provider.NotificationProvider;

import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    NotificationResult send(NotificationProvider.NotificationChannel channel, Notification notification);

    // Asincrónico (Opcional Requerido)
    CompletableFuture<NotificationResult> sendAsync(NotificationProvider.NotificationChannel channel, Notification notification);
}

