package com.example.core.jwt.dto;

public record Token(
        int expiration,
        String secret
) {
}
