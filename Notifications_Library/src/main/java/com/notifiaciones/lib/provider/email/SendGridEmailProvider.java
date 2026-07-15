package com.notifiaciones.lib.provider.email;

import com.notifiaciones.lib.domain.Notification;
import com.notifiaciones.lib.domain.NotificationResult;
import com.notifiaciones.lib.exception.NotificationException;
import com.notifiaciones.lib.exception.ValidationException;
import com.notifiaciones.lib.provider.NotificationProvider;

import java.util.UUID;

public class SendGridEmailProvider implements NotificationProvider {
    private final String apiKey;

    public SendGridEmailProvider(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) throw new IllegalArgumentException("API Key requerida");
        this.apiKey = apiKey;
    }

    @Override
    public NotificationResult send(Notification notification) throws NotificationException {
        String email = notification.getDestination().target();
        // Validación básica
        if (!email.contains("@")) {
            throw new ValidationException("Formato de Email inválido: " + email);
        }

        String subject = notification.getDestination().metadata().getOrDefault("subject", "Sin Asunto");

        // Simulación de envío exitoso
        System.out.printf("[SendGrid] Enviando Correo a %s. Asunto: %s. Contenido: %s%n", email, subject, notification.getContent());
        return NotificationResult.ofSuccess(UUID.randomUUID().toString(), 1);
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL;
    }
}
