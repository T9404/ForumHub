package com.example.exception.event;

import com.example.exception.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.slf4j.event.Level;

@Getter
@AllArgsConstructor
public enum UserEvent implements EventInfo {
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, Level.INFO),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO),
    USER_NOT_VERIFIED(HttpStatus.BAD_REQUEST, Level.INFO),
    USER_ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, Level.INFO),
    USER_NOT_BLOCKED(HttpStatus.BAD_REQUEST, Level.INFO),
    PERMISSION_DENIED(HttpStatus.BAD_REQUEST, Level.INFO),
    USER_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, Level.INFO);

    private final HttpStatus status;
    private final Level level;
}
