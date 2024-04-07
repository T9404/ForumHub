package com.example.rest.auth.v1.request;

import jakarta.validation.constraints.Email;

public record ResendTokenRequest(
        @Email(message = "Email should be valid")
        String email
) {
}
