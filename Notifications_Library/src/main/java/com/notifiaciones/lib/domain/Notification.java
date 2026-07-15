package com.notifiaciones.lib.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Notification {
    private final String content;
    private final NotificationDestination destination;
}
