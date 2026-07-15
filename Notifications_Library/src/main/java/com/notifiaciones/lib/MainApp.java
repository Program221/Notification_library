package com.notifiaciones.lib;

import com.notifiaciones.lib.config.NotificationClientBuilder;
import com.notifiaciones.lib.domain.Notification;
import com.notifiaciones.lib.domain.NotificationDestination;
import com.notifiaciones.lib.domain.NotificationResult;
import com.notifiaciones.lib.provider.NotificationProvider;
import com.notifiaciones.lib.provider.email.SendGridEmailProvider;
import com.notifiaciones.lib.provider.sms.TwilioSmsProvider;
import com.notifiaciones.lib.service.NotificationService;

import java.util.Map;

public class MainApp {
    public static void main(String[] args) {
        // 1. Configuración limpia mediante código puro (Fluent Builder)
        NotificationService notificationService = new NotificationClientBuilder()
                .registerProvider(new SendGridEmailProvider("SG.api_key_test"))
                .registerProvider(new TwilioSmsProvider("AC_twilio_sid"))
                .withRetries(3)
                .build();

        // 2. Construcción de Notificación de Email
        Notification emailNotif = Notification.builder()
                .content("<h1>¡Bienvenido a la plataforma!</h1>")
                .destination(NotificationDestination.of("alexanderestela22@gmail.com", Map.of("subject", "Registro Exitoso")))
                .build();

        // 3. Envío Sincrónico
        NotificationResult emailResult = notificationService.send(NotificationProvider.NotificationChannel.EMAIL, emailNotif);
        System.out.println("Resultado Email -> Exitoso: " + emailResult.success() + " | ID: " + emailResult.messageId());

        // 4. Construcción y Envío Asincrónico de un SMS
        Notification smsNotif = Notification.builder()
                .content("Tu código de verificación es 4589")
                .destination(NotificationDestination.of("+51999888777"))
                .build();

        notificationService.sendAsync(NotificationProvider.NotificationChannel.SMS, smsNotif)
                .thenAccept(result -> System.out.println("Resultado SMS Async -> Exitoso: " + result.success()));
    }

}
