package com.notifiaciones.lib.config;

import com.notifiaciones.lib.provider.NotificationProvider;
import com.notifiaciones.lib.service.DefaultNotificationService;
import com.notifiaciones.lib.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

public class NotificationClientBuilder {
    private final List<NotificationProvider> providers = new ArrayList<>();
    private int maxRetries = 3;
    public NotificationClientBuilder registerProvider(NotificationProvider provider) {
        this.providers.add(provider);
        return this;
    }

    public NotificationClientBuilder withRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public NotificationService build() {
        if (providers.isEmpty()) {
            throw new IllegalStateException("Debes registrar al menos un proveedor de notificaciones.");
        }
        return new DefaultNotificationService(providers, maxRetries);
    }
}