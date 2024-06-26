package com.example.contract.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.UUID;

public record UserDto(
        @JsonProperty("user_id")
        UUID userId,

        String username,

        String email,

        @JsonProperty("full_name")
        String fullName,

        @JsonProperty("phone_number")
        String phoneNumber,

        Set<String> roles
) {
}
