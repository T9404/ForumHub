package com.example.wishlist.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "outbox.message")
public class WishlistKafkaProperty {
    private int pendingTimeoutInSeconds;
    private int retryMaxAttempts;
    private int kafkaTransformTimeout;
    private long kafkaSendTimeout;
}
