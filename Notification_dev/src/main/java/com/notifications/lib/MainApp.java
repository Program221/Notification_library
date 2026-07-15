package com.notifications.lib;

import com.notifiaciones.lib.config.NotificationClientBuilder;
import com.notifiaciones.lib.provider.email.SendGridEmailProvider;
import com.notifiaciones.lib.provider.sms.TwilioSmsProvider;
import com.notifiaciones.lib.service.NotificationService;

/**
 * Hello world!
 */
public class MainApp {
    public static void main(String[] args) {

        NotificationService miLibreriaService = new NotificationClientBuilder()
                .registerProvider(new SendGridEmailProvider("API_KEY_REAL_O_TEST"))
                .registerProvider(new TwilioSmsProvider("TWILIO_SID_TEST"))
                .withRetries(3) // Sistema de reintentos automático
                .build();

        // STEP 2: Instancias tu nueva clase de negocio pasándole la librería configurada
        NotificationManager manager = new NotificationManager(miLibreriaService);

        // STEP 3: Usas los métodos de tu nueva clase de forma limpia
        manager.registrarNuevoUsuario("alexanderestela22@gmail.com", "Alexander Estela");

        manager.enviarAlertaUrgente("+51999888777", "Intento de inicio de sesión sospechoso.");
    }
}

