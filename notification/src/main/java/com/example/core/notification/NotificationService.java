package com.example.core.notification;

import com.example.contract.notification.NotificationDto;
import com.example.core.notification.communication.CommunicationService;
import com.example.core.notification.repository.NotificationRepository;
import com.example.core.notification.repository.dto.NotificationFilterDto;
import com.example.core.notification.repository.entity.NotificationEntity;
import com.example.public_interface.notification.GetNotificationDto;
import com.example.rest.notification.dto.GetNotification;
import com.example.rest.notification.dto.GetUnreadNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.example.core.converter.OffsetDateTimeConverter.convert;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final CommunicationService communicationService;

    public void notify(NotificationDto notificationDto) {
        if (notificationRepository.existsById(notificationDto.notificationId())) {
            return;
        }

        var notification = NotificationEntity.builder()
                .notificationId(notificationDto.notificationId())
                .createdAt(convert(notificationDto.createdAt()))
                .content(notificationDto.content())
                .title(notificationDto.title())
                .isRead(false)
                .isImportant(notificationDto.isImportant())
                .receiverId(UUID.fromString(notificationDto.receiverId()))
                .build();

        communicationService.communicate(notification, notificationDto.deliveryChannels());
        notificationRepository.save(notification);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Page<GetNotificationDto> getNotifications(GetNotification dto, Pageable pageable) {
        var notificationFilterDto = NotificationFilterDto.builder()
                .title(dto.title())
                .content(dto.content())
                .receiverId(dto.userId())
                .build();

        Page<NotificationEntity> notifications =
                notificationRepository.findImportantNotificationsByReceiverId(notificationFilterDto, pageable);

        notifications.get().forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);

        return notifications.map(this::mapToGetNotificationDto);
    }

    public GetUnreadNotification getUnreadNotificationsCount(UUID receiverId) {
        int count = notificationRepository.countUnreadNotificationsByReceiverId(receiverId);
        return new GetUnreadNotification(count);
    }

    private GetNotificationDto mapToGetNotificationDto(NotificationEntity notificationEntity) {
        return new GetNotificationDto(
                notificationEntity.getNotificationId(),
                notificationEntity.getTitle(),
                notificationEntity.getContent(),
                notificationEntity.getCreatedAt()
        );
    }

}
