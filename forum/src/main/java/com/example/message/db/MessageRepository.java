package com.example.message.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID>, FilterMessageRepository {
    List<MessageEntity> findByContentContainingIgnoreCase(String content);

    @Query("SELECT m FROM MessageEntity m JOIN FETCH m.topic WHERE m.messageId = :messageId")
    Optional<MessageEntity> findByIdWithTopicFetch(UUID messageId);
}
