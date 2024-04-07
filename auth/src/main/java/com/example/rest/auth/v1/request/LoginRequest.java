package com.example.rest.auth.v1.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Invalid password: EMPTY password")
        @NotNull(message = "Invalid password: password is NULL")
        @Size(min = 5, max = 50, message = "Password should be between 5 and 50 characters")
        String password
) {
}
