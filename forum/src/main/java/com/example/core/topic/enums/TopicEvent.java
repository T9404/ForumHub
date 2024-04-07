package com.example.core.topic.enums;

import com.example.core.common.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TopicEvent implements EventInfo {
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO),
    INVALID_TOPIC_SORTING(HttpStatus.BAD_REQUEST, Level.INFO);

    private final HttpStatus status;
    private final Level level;

    @Override
    public String toString() {
        return this.name();
    }
}
