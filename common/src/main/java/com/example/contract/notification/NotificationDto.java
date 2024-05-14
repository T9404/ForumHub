package com.example.contract.notification;

import lombok.Builder;

import java.util.List;

@Builder
public record NotificationDto(
        String notificationId,
        String title,
        String content,
        String receiverId,
        long createdAt,
        boolean isImportant,
        List<DeliveryChannel> deliveryChannels
) {
}
