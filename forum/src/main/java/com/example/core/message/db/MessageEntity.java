package com.example.core.message.db;

import com.example.core.topic.db.TopicEntity;
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
@Table(name = "message")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "topic_id")
    private TopicEntity topic;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "modification_at")
    private OffsetDateTime modificationAt;

    @Column(name = "creator_id")
    private UUID creatorId;
}
