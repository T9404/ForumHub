package com.example.security.dto.token;

public record Token(
        int expiration,
        String secret
) {
}
