package com.example.core.notification.repository;

import com.example.core.notification.repository.dto.NotificationFilterDto;
import com.example.core.notification.repository.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterNotificationRepository {
    Page<NotificationEntity> findImportantNotificationsByReceiverId(NotificationFilterDto dto, Pageable pageable);
}
