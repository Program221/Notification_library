package com.notifiaciones.lib.provider.push;

import com.notifiaciones.lib.domain.Notification;
import com.notifiaciones.lib.domain.NotificationResult;
import com.notifiaciones.lib.exception.NotificationException;
import com.notifiaciones.lib.provider.NotificationProvider;

import java.util.UUID;

public class FirebasePushProvider implements NotificationProvider {
    private final String serverKey;

    public FirebasePushProvider(String serverKey) {
        this.serverKey = serverKey;
    }

    @Override
    public NotificationResult send(Notification notification) throws NotificationException {
        String token = notification.getDestination().target();
        String title = notification.getDestination().metadata().getOrDefault("title", "Notificación");

        System.out.printf("[FCM] Enviando Push al Token %s. Título: %s%n", token, title);
        return NotificationResult.ofSuccess(UUID.randomUUID().toString(), 1);
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.PUSH_NOTIFICATION;
    }
}
