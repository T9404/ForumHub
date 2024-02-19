package com.example.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
        OffsetDateTime time,
        String message,
        int code
) {
}
