package com.example.public_interface.error;

import java.time.OffsetDateTime;

public record ErrorResponseDto(
        OffsetDateTime time,
        String message,
        int code
) {
}
