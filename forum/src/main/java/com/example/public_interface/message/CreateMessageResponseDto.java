package com.example.public_interface.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateMessageResponseDto(
        @JsonProperty("message_id")
        UUID messageId
) {
}
