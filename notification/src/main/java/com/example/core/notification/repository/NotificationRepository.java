package com.example.core.notification.repository;

import com.example.core.notification.repository.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;


public interface NotificationRepository extends JpaRepository<NotificationEntity, String>, FilterNotificationRepository {
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.receiverId = ?1 AND n.isRead = false")
    int countUnreadNotificationsByReceiverId(UUID receiverId);
}
