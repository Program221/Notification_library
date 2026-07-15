package com.notifiaciones.lib.provider.sms;

import com.notifiaciones.lib.domain.Notification;
import com.notifiaciones.lib.domain.NotificationResult;
import com.notifiaciones.lib.exception.NotificationException;
import com.notifiaciones.lib.exception.ValidationException;
import com.notifiaciones.lib.provider.NotificationProvider;

import java.util.UUID;

public class TwilioSmsProvider implements NotificationProvider {
    private final String accountSid;

    public TwilioSmsProvider(String accountSid) {
        this.accountSid = accountSid;
    }
    @Override
    public NotificationResult send(Notification notification) throws NotificationException {
        String phone = notification.getDestination().target();
        if (phone.length() < 8) {
            throw new ValidationException("Número de teléfono inválido: " + phone);
        }

        System.out.printf("[Twilio] Enviando SMS a %s: %s%n", phone, notification.getContent());
        return NotificationResult.ofSuccess(UUID.randomUUID().toString(), 1);
    }


    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.SMS;
    }
}
