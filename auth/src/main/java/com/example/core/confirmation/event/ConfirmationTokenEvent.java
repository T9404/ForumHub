package com.example.core.confirmation.event;

import com.example.exception.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ConfirmationTokenEvent implements EventInfo {
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, Level.INFO),
    TOKEN_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, Level.INFO);

    private final HttpStatus status;
    private final Level level;
}
