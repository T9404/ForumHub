package com.example.core.notification.communication;

import com.example.contract.notification.DeliveryChannel;
import com.example.contract.notification.NotificationDto;
import com.example.core.notification.factory.NotificationHandlerFactory;
import com.example.core.notification.repository.entity.NotificationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunicationService {
    private final NotificationHandlerFactory notificationHandlerFactory;

    public void communicate(NotificationEntity notification, List<DeliveryChannel> deliveryChannelList) {
        Set<DeliveryChannel> channelsToSend = getChannelsToSend(deliveryChannelList);

        for (DeliveryChannel channel: channelsToSend) {
            notificationHandlerFactory.getNotificationHandler(channel)
                    .ifPresent(notificationChannel -> notificationChannel.execute(notification));
        }
    }

    private Set<DeliveryChannel> getChannelsToSend(List<DeliveryChannel> deliveryChannelList) {
        if (deliveryChannelList.contains(DeliveryChannel.ALL)) {
            return new HashSet<>(Arrays.asList(DeliveryChannel.values()));
        }

        return new HashSet<>(deliveryChannelList);
    }
}
