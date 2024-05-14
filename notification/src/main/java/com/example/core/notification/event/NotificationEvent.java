package com.example.core.notification.event;

import com.example.exception.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationEvent implements EventInfo {
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO),
    NOTIFICATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, Level.INFO);

    private final HttpStatus status;
    private final Level level;
}
