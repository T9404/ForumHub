package com.example.error.response;

import java.time.OffsetDateTime;

public record ErrorResponseDto(
        OffsetDateTime time,
        String message,
        int code
) {
}
