package com.example.core.attachment.db;

import com.example.core.message.db.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<AttachmentEntity, UUID> {
    void deleteAllByMessage(MessageEntity message);

    @Modifying
    @Query(value = "SELECT * FROM attachment WHERE message_id = ?1", nativeQuery = true)
    Set<AttachmentEntity> findAllByMessageId(UUID messageId);
}
