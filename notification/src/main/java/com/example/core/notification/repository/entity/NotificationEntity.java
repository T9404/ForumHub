package com.example.core.notification.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class NotificationEntity {

    @Id
    @Column(name = "notification_id")
    private String notificationId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "receiver_id")
    private UUID receiverId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "is_important")
    private boolean isImportant;
}
