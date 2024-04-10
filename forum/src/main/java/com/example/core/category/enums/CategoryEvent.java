package com.example.core.category.enums;

import com.example.exception.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryEvent implements EventInfo {
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO),
    CATEGORY_IS_NOT_LEAF(HttpStatus.BAD_REQUEST, Level.INFO),
    CATEGORY_HAS_TOPICS(HttpStatus.BAD_REQUEST, Level.INFO),
    CATEGORY_INVALID_ID(HttpStatus.BAD_REQUEST, Level.INFO);

    private final HttpStatus status;
    private final Level level;

    @Override
    public String toString() {
        return this.name();
    }
}
