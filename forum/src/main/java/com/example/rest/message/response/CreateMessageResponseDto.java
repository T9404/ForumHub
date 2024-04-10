package com.example.rest.message.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateMessageResponseDto(
        @JsonProperty("message_id")
        UUID messageId
) {
}
