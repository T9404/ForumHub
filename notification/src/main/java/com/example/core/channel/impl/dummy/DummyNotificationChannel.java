package com.example.core.channel.impl.dummy;

import com.example.core.channel.NotificationChannel;
import com.example.core.notification.repository.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class DummyNotificationChannel implements NotificationChannel {

    @Override
    public void execute(NotificationEntity notification) {
        // This method is intentionally left empty as it is a dummy implementation for notifications with ALL status.
        // Notifications with ALL status are handled before reaching this point.
    }
}