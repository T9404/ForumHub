package com.example.rest.controller.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UpdateMessageRequestDto(
        @JsonProperty("message_id")
        UUID messageId,

        String content,

        @JsonProperty("topic_id")
        UUID topicId
) {
}
