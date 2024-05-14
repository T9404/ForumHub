package com.example.rest.notification.dto;

import java.util.UUID;

public record GetNotification(
    String title,
    String content,
    UUID userId
) {
}
