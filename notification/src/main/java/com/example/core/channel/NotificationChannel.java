package com.example.core.channel;

import com.example.core.notification.repository.entity.NotificationEntity;

public interface NotificationChannel {
    void execute(NotificationEntity notification);
}
