package com.example.public_interface.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateMessageRequestDto(
        String content,

        @JsonProperty("topic_id")
        UUID topicId,

        @JsonProperty("creator_id")
        UUID creatorId
) {
}
