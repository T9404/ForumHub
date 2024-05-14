package com.example.public_interface.notification;

import java.time.OffsetDateTime;

public record GetNotificationDto(
        String notificationId,
        String title,
        String content,
        OffsetDateTime createdAt
) {
}
