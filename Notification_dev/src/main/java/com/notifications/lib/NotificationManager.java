package com.notifications.lib;

import com.notifiaciones.lib.domain.Notification;
import com.notifiaciones.lib.domain.NotificationDestination;
import com.notifiaciones.lib.domain.NotificationResult;
import com.notifiaciones.lib.provider.NotificationProvider;
import com.notifiaciones.lib.service.NotificationService;

import java.util.Map;

public class NotificationManager {
    private final NotificationService notificationService;
    public NotificationManager(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
        public void registrarNuevoUsuario(String email, String nombre) {
            System.out.println("[Business Log] Procesando registro para: " + nombre);

            // 1. Construimos la notificación usando el Builder de la librería
            Notification correoRegistro = Notification.builder()
                    .content("¡Hola " + nombre + "! Tu cuenta ha sido creada con éxito.")
                    .destination(NotificationDestination.of(email, Map.of("subject", "Bienvenido a bordo")))
                    .build();

            // 2. Enviamos usando la interfaz común (es transparente qué proveedor se usa detrás)
            NotificationResult result = notificationService.send(NotificationProvider.NotificationChannel.EMAIL, correoRegistro);

            if (result.success()) {
                System.out.println("[Business Log] Correo enviado con ID: " + result.messageId());
            } else {
                System.err.println("[Business Log] Error al enviar correo: " + result.errorMessage());
            }
        }

        /**
         * Ejemplo de método de negocio: Alerta urgente por SMS de forma asíncrona
         */
        public void enviarAlertaUrgente(String telefono, String mensaje) {
            Notification smsAlerta = Notification.builder()
                    .content("[ALERTA] " + mensaje)
                    .destination(NotificationDestination.of(telefono))
                    .build();

            // Enviamos de forma asíncrona para no bloquear el hilo principal de la app
            notificationService.sendAsync(NotificationProvider.NotificationChannel.SMS, smsAlerta)
                    .thenAccept(result -> {
                        if (result.success()) {
                            System.out.println("[Business Log] SMS enviado de forma asíncrona.");
                        }
                    });
        }
    }


