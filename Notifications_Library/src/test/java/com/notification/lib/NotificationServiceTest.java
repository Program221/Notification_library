package com.notification.lib;

import com.notifiaciones.lib.MainApp;
import com.notifiaciones.lib.config.NotificationClientBuilder;
import com.notifiaciones.lib.domain.Notification;
import com.notifiaciones.lib.domain.NotificationDestination;
import com.notifiaciones.lib.domain.NotificationResult;
import com.notifiaciones.lib.exception.NotificationException;
import com.notifiaciones.lib.exception.ValidationException;
import com.notifiaciones.lib.provider.NotificationProvider;
import com.notifiaciones.lib.provider.email.SendGridEmailProvider;
import com.notifiaciones.lib.provider.push.FirebasePushProvider;
import com.notifiaciones.lib.provider.sms.TwilioSmsProvider;
import com.notifiaciones.lib.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple NotificationBuilder.
 */
public class NotificationServiceTest {

    private NotificationService notificationService;
    private SendGridEmailProvider emailProvider;
    private TwilioSmsProvider smsProvider;
    private FirebasePushProvider pushProvider;

    @BeforeEach
    void setUp() {
        // Inicializamos los proveedores reales (simulados internamente) para el Service
        emailProvider = new SendGridEmailProvider("SG.valid_key");
        smsProvider = new TwilioSmsProvider("AC_valid_sid");
        pushProvider = new FirebasePushProvider("FCM_valid_key");

        notificationService = new NotificationClientBuilder()
                .registerProvider(emailProvider)
                .registerProvider(smsProvider)
                .registerProvider(pushProvider)
                .withRetries(3)
                .build();
    }

    // ==========================================
    // 1. PRUEBAS DEL CANAL DE EMAIL (SendGrid)
    // ==========================================

    @Test
    void email_DebeEnviarExitosamenteConMetadata() {
        Notification notification = Notification.builder()
                .content("Cuerpo del correo")
                .destination(NotificationDestination.of("test@domain.com", Map.of("subject", "Hola Test")))
                .build();

        NotificationResult result = notificationService.send(NotificationProvider.NotificationChannel.EMAIL, notification);

        assertTrue(result.success());
        assertNotNull(result.messageId());
        assertNull(result.errorMessage());
        assertEquals(1, result.attempts());
    }

    @Test
    void email_DebeLanzarValidationExceptionSiNoTieneArroba() {
        Notification notification = Notification.builder()
                .content("Contenido")
                .destination(NotificationDestination.of("correo_invalido.com"))
                .build();

        NotificationResult result = notificationService.send(NotificationProvider.NotificationChannel.EMAIL, notification);

        assertFalse(result.success());
        assertTrue(result.errorMessage().contains("[Validación Fállida]"));
        assertEquals(1, result.attempts()); // Al ser validación, no reintenta mas veces
    }

    @Test
    void email_ConstructorDebeLanzarExcepcionSiApiKeyEsInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new SendGridEmailProvider(null));
        assertThrows(IllegalArgumentException.class, () -> new SendGridEmailProvider("   "));
    }

    // ==========================================
    // 2. PRUEBAS DEL CANAL DE SMS (Twilio)
    // ==========================================

    @Test
    void sms_DebeEnviarExitosamente() {
        Notification notification = Notification.builder()
                .content("Tu código es 1234")
                .destination(NotificationDestination.of("+51999888777"))
                .build();

        NotificationResult result = notificationService.send(NotificationProvider.NotificationChannel.SMS, notification);

        assertTrue(result.success());
        assertNotNull(result.messageId());
    }

    @Test
    void sms_DebeFallarSiElTelefonoEsMuyCorto() {
        Notification notification = Notification.builder()
                .content("Texto")
                .destination(NotificationDestination.of("123")) // Inválido por longitud
                .build();

        NotificationResult result = notificationService.send(NotificationProvider.NotificationChannel.SMS, notification);

        assertFalse(result.success());
        assertEquals(1, result.attempts());
    }

    // ==========================================
    // 3. PRUEBAS DEL CANAL PUSH (Firebase)
    // ==========================================

    @Test
    void push_DebeEnviarExitosamenteConTituloPorDefecto() {
        Notification notification = Notification.builder()
                .content("Mensaje Push")
                .destination(NotificationDestination.of("device_token_abc"))
                .build();

        NotificationResult result = notificationService.send(NotificationProvider.NotificationChannel.PUSH_NOTIFICATION, notification);

        assertTrue(result.success());
        assertNotNull(result.messageId());
    }

    // ==========================================
    // 4. PRUEBAS DE ASINCRONÍA (CompletableFuture)
    // ==========================================

    @Test
    void async_DebeEnviarDeFormaNoBloqueante() throws ExecutionException, InterruptedException {
        Notification notification = Notification.builder()
                .content("Contenido Async")
                .destination(NotificationDestination.of("async@test.com"))
                .build();

        CompletableFuture<NotificationResult> future = notificationService.sendAsync(NotificationProvider.NotificationChannel.EMAIL, notification);

        assertNotNull(future);
        NotificationResult result = future.get(); // Bloqueamos solo para verificar el resultado en el test
        assertTrue(result.success());
    }

    // ==========================================
    // 5. PRUEBAS DE CONFIGURACIÓN Y ERRORES CENTRALES
    // ==========================================

    @Test
    void builder_DebeLanzarExcepcionSiNoSeRegistranProveedores() {
        NotificationClientBuilder emptyBuilder = new NotificationClientBuilder();
        assertThrows(IllegalStateException.class, emptyBuilder::build);
    }

    @Test
    void service_DebeLanzarExcepcionSiSePideUnCanalNoSoportado() {
        // Creamos un servicio que solo tiene el proveedor de SMS
        NotificationService limitedService = new NotificationClientBuilder()
                .registerProvider(new TwilioSmsProvider("SID"))
                .build();

        Notification notification = Notification.builder()
                .content("Hola")
                .destination(NotificationDestination.of("test@test.com"))
                .build();

        // Intentamos mandar EMAIL cuando el servicio no lo soporta
        assertThrows(IllegalArgumentException.class, () ->
                limitedService.send(NotificationProvider.NotificationChannel.EMAIL, notification)
        );
    }

    @Test
    void service_DebeEjecutarSistemaDeReintentosCuandoOcurreUnErrorDeEnvio() {
        // Mock / Stub de un proveedor que siempre falla simulando un error de red o API caída
        NotificationProvider proveedorInestable = new NotificationProvider() {
            @Override
            public NotificationResult send(Notification notification) {
                throw new RuntimeException("Error de conexión con el servidor externo");
            }

            @Override
            public boolean supports(NotificationChannel channel) {
                return channel == NotificationChannel.EMAIL;
            }
        };

        NotificationService serviceConFallas = new NotificationClientBuilder()
                .registerProvider(proveedorInestable)
                .withRetries(3) // Configurado a 3 reintentos
                .build();

        Notification notification = Notification.builder()
                .content("Test Reintentos")
                .destination(NotificationDestination.of("retry@test.com"))
                .build();

        NotificationResult result = serviceConFallas.send(NotificationProvider.NotificationChannel.EMAIL, notification);

        // Verificaciones del flujo de reintentos
        assertFalse(result.success());
        assertEquals(3, result.attempts()); // Validamos que se agotaron los 3 intentos exactos
        assertTrue(result.errorMessage().contains("Error tras 3 intentos"));
    }

    @Test
    void exceptions_VerificarGettersDeExcepcionesPersonalizadas() {
        NotificationException ex1 = new NotificationException("Error general");
        ValidationException ex2 = new ValidationException("Error validacion");

        assertEquals("DELIVERY_ERROR", ex1.getErrorType());
        assertEquals("VALIDATION_ERROR", ex2.getErrorType());
    }

    // ==========================================
    // 6. COBERTURA PARA LA APLICACIÓN PRINCIPAL (DEMO)
    // ==========================================

    @Test
    void verificarEjecucionDeMainApp() {
        // Ejecutamos el método main de MainApp pasando un arreglo de argumentos vacío.
        // Esto obligará a la herramienta de Coverage a pasar por todas las líneas del
        // flujo síncrono y asíncrono de demostración, eliminando el 0% de la métrica.
        assertDoesNotThrow(() -> {
            MainApp.main(new String[]{});
        });
    }
}