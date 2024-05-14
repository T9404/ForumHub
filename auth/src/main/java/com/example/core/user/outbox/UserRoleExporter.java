package com.example.core.user.outbox;

import com.example.contract.notification.DeliveryChannel;
import com.example.contract.notification.NotificationDto;
import com.example.core.user.outbox.exception.KafkaWaitingException;
import com.example.core.user.outbox.property.UserRoleExporterProperty;
import com.example.core.user.outbox.repository.entity.OutboxUserRoleEntity;
import com.example.core.user.outbox.repository.enums.OutboxUserRoleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Service
@RequiredArgsConstructor
public class UserRoleExporter {
    private final UserRoleExporterProperty property;
    private final OutboxUserRoleService outboxUserRoleService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${exporter.notification.source-topic}")
    private String topicName;

    @Scheduled(fixedRateString = "${outbox.user-role.exporter.fixed-rate}")
    public void export() {
        var outboxApplications = outboxUserRoleService.setAllPending(OutboxUserRoleStatus.NEW);

        var futures = outboxApplications.stream()
                .map(this::trySendApplication)
                .toArray(CompletableFuture[]::new);

        waitSendingEnd(CompletableFuture.allOf(futures), outboxApplications.size());
    }

    @Scheduled(fixedRateString = "${outbox.user-role.stalled.fixed-rate}")
    public void processStalled() {
        var outboxApplications = outboxUserRoleService.findAllByStatus(OutboxUserRoleStatus.PENDING);
        outboxApplications.forEach(this::processStalled);
    }

    private CompletableFuture<Void> trySendApplication(OutboxUserRoleEntity outboxUserRoleEntity) {
        try {
            var notificationDto = mapToNotificationExportDto(outboxUserRoleEntity);
            return kafkaTemplate.send(topicName, notificationDto.notificationId(), notificationDto)
                    .thenAccept(result -> outboxUserRoleService.updateStatus(outboxUserRoleEntity, OutboxUserRoleStatus.SENT))
                    .exceptionally(exception -> {
                        outboxUserRoleService.updateStatus(outboxUserRoleEntity, OutboxUserRoleStatus.ERROR);
                        return null;
                    });
        } catch (Exception e) {
            outboxUserRoleService.updateStatus(outboxUserRoleEntity, OutboxUserRoleStatus.ERROR);
            return CompletableFuture.completedFuture(null);
        }
    }

    private NotificationDto mapToNotificationExportDto(OutboxUserRoleEntity outboxUserRoleEntity) {
        return new NotificationDto(
                outboxUserRoleEntity.getOutboxId().toString(),
                "Your role has been updated",
                "You are " + outboxUserRoleEntity.getUser().getRoles().toString() + " now",
                outboxUserRoleEntity.getUser().getUserId().toString(),
                outboxUserRoleEntity.getUpdatedAt().toEpochSecond(),
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

    private void processStalled(OutboxUserRoleEntity outboxUserRoleEntity) {
        var applicationTime = outboxUserRoleEntity.getUpdatedAt();

        var pendingTimeout = property.getPendingTimeoutInSeconds();
        if (OffsetDateTime.now().isBefore(applicationTime.plusSeconds(pendingTimeout))) {
            return;
        }

        var maxAttempts = property.getRetryMaxAttempts();
        if (outboxUserRoleEntity.getRetryCount() >= maxAttempts) {
            outboxUserRoleService.updateStatus(outboxUserRoleEntity, OutboxUserRoleStatus.ERROR);
            return;
        }

        outboxUserRoleEntity.setRetryCount(outboxUserRoleEntity.getRetryCount() + 1);
        outboxUserRoleService.updateStatus(outboxUserRoleEntity, OutboxUserRoleStatus.NEW);
    }

}
