package com.example.rest.notification;

import com.example.core.notification.NotificationService;
import com.example.public_interface.notification.GetNotificationDto;
import com.example.rest.notification.dto.GetNotification;
import com.example.rest.notification.dto.GetUnreadNotification;
import com.example.security.dto.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public Page<GetNotificationDto> getNotifications(@RequestParam(required = false) String title,
                                                     @RequestParam(required = false) String content,
                                                     Pageable pageable,
                                                     @AuthenticationPrincipal User user) {
        var dto = new GetNotification(title, content, user.getUserId());
        return notificationService.getNotifications(dto, pageable);
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public GetUnreadNotification getUnreadNotificationsCount(@AuthenticationPrincipal User user) {
        return notificationService.getUnreadNotificationsCount(user.getUserId());
    }
}
