package com.example.wishlist;

import com.example.contract.notification.DeliveryChannel;
import com.example.contract.notification.NotificationDto;
import com.example.error.custom.KafkaWaitingException;
import com.example.message.db.MessageEntity;
import com.example.wishlist.db.WishlistEntity;
import com.example.wishlist.db.WishlistService;
import com.example.wishlist.property.WishlistKafkaProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Service
@RequiredArgsConstructor
public class WishlistNotificationService {
    private final WishlistService wishlistService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WishlistKafkaProperty property;

    @Value("${exporter.wishlist.source-topic}")
    private String topicName;

    @Async
    public void notifyAllSubscribers(MessageEntity message) {
        List<WishlistEntity> wishlistEntityList = wishlistService.findByTopicId(message.getTopic().getTopicId()).stream()
                .filter(wishlistEntity -> !wishlistEntity.getWishlistId().getUserId().equals(message.getCreatorId()))
                .toList();

        var futures = wishlistEntityList.stream()
                .map(wishlistEntity -> mapToNotificationExportDto(wishlistEntity, message))
                .map(this::trySendNotification)
                .toArray(CompletableFuture[]::new);

        waitSendingEnd(CompletableFuture.allOf(futures), wishlistEntityList.size());
    }

    private CompletableFuture<Void> trySendNotification(NotificationDto notificationDto) {
        try {
            return kafkaTemplate.send(topicName, notificationDto.notificationId(), notificationDto)
                    .thenAccept(result -> {
                    })
                    .exceptionally(exception -> null);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(null);
        }
    }

    private NotificationDto mapToNotificationExportDto(WishlistEntity entity, MessageEntity message) {
        return new NotificationDto(
                UUID.randomUUID().toString(),
                "New message in wish topic!",
                message.getContent(),
                entity.getWishlistId().getUserId().toString(),
                message.getCreatedAt().toEpochSecond(),
                true,
                List.of(DeliveryChannel.ALL)
        );
    }

    private void waitSendingEnd(CompletableFuture<Void> commonCompletableFuture, int countSendingElements) {
        var kafkaSendTimeout = property.getKafkaSendTimeout();
        var kafkaTransformTimeout = property.getKafkaTransformTimeout();
        final long awaitTime = countSendingElements * kafkaSendTimeout + kafkaTransformTimeout;

        try {
            commonCompletableFuture.get(awaitTime, MILLISECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException exception) {
            throw new KafkaWaitingException("Failed to wait for Kafka sending", exception);
        }
    }
}
