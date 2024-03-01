package com.example.core.message;

import com.example.rest.configuration.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MessageEvent implements EventInfo {
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO);

    private final HttpStatus status;
    private final Level level;

    @Override
    public String toString() {
        return this.name();
    }
}