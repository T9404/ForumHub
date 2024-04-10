package com.example.rest.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateMessageRequestDto(
        @JsonProperty("message_id")
        UUID messageId,

        String content,

        @JsonProperty("topic_id")
        UUID topicId
) {
}
