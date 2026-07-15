package com.notifiaciones.lib.domain;

import java.util.Collections;
import java.util.Map;

public record NotificationDestination(String target, // Email, teléfono, o token del dispositivo
                                      Map<String, String> metadata
                                      // Para campos extra como 'subject', 'title', etc.)) {
) {
    public static NotificationDestination of(String target) {
        return new NotificationDestination(target, Collections.emptyMap());
    }

    public static NotificationDestination of(String target, Map<String, String> metadata) {
        return new NotificationDestination(target, Map.copyOf(metadata));
    }
}

