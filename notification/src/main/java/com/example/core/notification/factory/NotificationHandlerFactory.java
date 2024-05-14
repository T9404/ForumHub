package com.example.core.notification.factory;

import com.example.contract.notification.DeliveryChannel;
import com.example.core.channel.NotificationChannel;
import com.example.core.channel.impl.dummy.DummyNotificationChannel;
import com.example.core.channel.impl.mail.MailNotificationChannel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class NotificationHandlerFactory {
    private final Map<DeliveryChannel, NotificationChannel> handlers = new HashMap<>();

    public NotificationHandlerFactory(MailNotificationChannel mailNotificationChannel, DummyNotificationChannel dummyNotificationChannel) {
        handlers.put(DeliveryChannel.EMAIL, mailNotificationChannel);
        handlers.put(DeliveryChannel.ALL, dummyNotificationChannel);
    }

    public Optional<NotificationChannel> getNotificationHandler(DeliveryChannel deliveryChannel) {
        return Optional.ofNullable(handlers.get(deliveryChannel));
    }
}
