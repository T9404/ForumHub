package com.example.core.jwt.event;

import com.example.exception.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JwtTokenEvent implements EventInfo {
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, Level.INFO),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, Level.INFO),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, Level.INFO),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, Level.INFO),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, Level.INFO);

    private final HttpStatus status;
    private final Level level;
}
