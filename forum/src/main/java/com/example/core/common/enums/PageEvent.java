package com.example.core.common.enums;

import com.example.core.common.exception.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PageEvent implements EventInfo {
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, Level.INFO),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, Level.INFO),
    INVALID_ORDER_SORTING_TYPE(HttpStatus.BAD_REQUEST, Level.INFO);

    private final HttpStatus status;
    private final Level level;

    @Override
    public String toString() {
        return this.name();
    }
}
