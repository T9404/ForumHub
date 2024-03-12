package com.example.rest.controller.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateMessageRequestDto(
        String content,

        @JsonProperty("topic_id")
        UUID topicId,

        @JsonProperty("creator_id")
        UUID creatorId
) {
}
