package com.example.core.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID>, FilterMessageRepository {
    List<MessageEntity> findByContentContainingIgnoreCase(String content);
}
