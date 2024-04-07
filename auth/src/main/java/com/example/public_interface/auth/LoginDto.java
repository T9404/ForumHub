package com.example.public_interface.auth;

import lombok.Builder;

@Builder
public record LoginDto(
        String email,
        String password
) {
}
