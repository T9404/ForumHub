package com.example.attachment.enums;

import com.example.exception.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AttachmentEvent implements EventInfo {
    ATTACHMENT_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO);

    private final HttpStatus status;
    private final Level level;
}
