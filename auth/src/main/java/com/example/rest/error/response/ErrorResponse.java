package com.example.rest.error.response;

public record ErrorResponse(
        String time,
        String message,
        int code
) {
}

