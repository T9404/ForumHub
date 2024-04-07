package com.example.rest.admin.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
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
        List<String> roles
) {
}
