package com.notifiaciones.lib.service;

import com.notifiaciones.lib.domain.Notification;
import com.notifiaciones.lib.domain.NotificationResult;
import com.notifiaciones.lib.exception.ValidationException;
import com.notifiaciones.lib.provider.NotificationProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultNotificationService implements NotificationService{
    private final List<NotificationProvider> providers;
    private final int maxRetries;
    private final ExecutorService executor;
    public DefaultNotificationService(List<NotificationProvider> providers, int maxRetries) {
        this.providers = providers;
        this.maxRetries = maxRetries;
        // Hilo virtual de Java 21 optimizado para operaciones I/O simuladas
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }
    @Override
    public NotificationResult send(NotificationProvider.NotificationChannel channel, Notification notification) {
        NotificationProvider provider = providers.stream()
                .filter(p -> p.supports(channel))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontró proveedor para el canal: " + channel));

        int attempts = 0;
        while (attempts < maxRetries) {
            attempts++;
            try {
                return provider.send(notification);
            } catch (ValidationException e) {
                // Si es un error de validación, no tiene sentido reintentar
                return NotificationResult.ofFailure("[Validación Fállida] " + e.getMessage(), attempts);
            } catch (Exception e) {
                if (attempts >= maxRetries) {
                    return NotificationResult.ofFailure("Error tras " + attempts + " intentos: " + e.getMessage(), attempts);
                }
                System.out.println(">>> Intento " + attempts + " fallido para " + channel + ". Reintentando...");
            }
        }
        return NotificationResult.ofFailure("Error desconocido", attempts);
    }

    @Override
    public CompletableFuture<NotificationResult> sendAsync(NotificationProvider.NotificationChannel channel, Notification notification) {
        return CompletableFuture.supplyAsync(() -> send(channel, notification), executor);
    }
}
