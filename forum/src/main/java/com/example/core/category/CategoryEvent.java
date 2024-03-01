package com.example.core.category;

import com.example.rest.configuration.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryEvent implements EventInfo {
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO);

    private final HttpStatus status;
    private final Level level;

    @Override
    public String toString() {
        return this.name();
    }
}
