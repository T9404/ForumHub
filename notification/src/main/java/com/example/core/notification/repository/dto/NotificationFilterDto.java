package com.example.core.notification.repository.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record NotificationFilterDto(
        UUID receiverId,
        String title,
        String content
) {
}
