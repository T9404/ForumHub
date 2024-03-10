package com.example.core.message.enums;

import com.example.core.common.exception.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MessageEvent implements EventInfo {
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO),
    TOPIC_ID_IS_REQUIRED(HttpStatus.BAD_REQUEST, Level.WARN);

    private final HttpStatus status;
    private final Level level;

    @Override
    public String toString() {
        return this.name();
    }
}
