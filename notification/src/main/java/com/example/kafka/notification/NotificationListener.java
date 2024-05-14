package com.example.kafka.notification;

import com.example.contract.notification.DeliveryChannel;
import com.example.contract.notification.NotificationDto;
import com.example.core.notification.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NotificationService notificationService;
    private final ObjectMapper jsonMapper;

    @Value("${exporter.notification.error-topic}")
    private String errorTopicName;

    @KafkaListener(
            topics = "${exporter.notification.source-topic}",
            groupId = "${consumer.kafka.group-id}",
            batch = "true"
    )
    public void consume(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        List<CompletableFuture<Void>> futures = records.stream()
                .map(consumerRecord -> CompletableFuture.runAsync(() -> processRecord(consumerRecord)))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.thenRun(ack::acknowledge);
    }

    private void processRecord(ConsumerRecord<String, String> consumerRecord) {
        try {
            var notificationDto = getEvent(consumerRecord);
            notificationService.notify(notificationDto);
        } catch (Exception exception) {
            kafkaTemplate.send(errorTopicName, consumerRecord.key(), consumerRecord.value());
        }
    }

    @SneakyThrows(JsonProcessingException.class)
    private NotificationDto getEvent(ConsumerRecord<String, String> consumerRecord) {
        JsonNode payload = jsonMapper.readTree(consumerRecord.value());

        List<DeliveryChannel> deliveryChannels = new ArrayList<>();
        JsonNode deliveryChannelsNode = payload.get("deliveryChannels");
        if (deliveryChannelsNode.isArray()) {
            for (JsonNode channelNode : deliveryChannelsNode) {
                deliveryChannels.add(DeliveryChannel.valueOf(channelNode.asText()));
            }
        }

        return NotificationDto.builder()
                .notificationId(payload.get("notificationId").asText())
                .title(payload.get("title").asText())
                .content(payload.get("content").asText())
                .receiverId(payload.get("receiverId").asText())
                .createdAt(payload.get("createdAt").asLong())
                .isImportant(payload.get("isImportant").asBoolean())
                .deliveryChannels(deliveryChannels)
                .build();
    }
}
