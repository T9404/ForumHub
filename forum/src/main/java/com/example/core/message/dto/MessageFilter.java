package com.example.core.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record MessageFilter(
        String content,

        OffsetDateTime start,

        OffsetDateTime end,

        @JsonProperty("creator_id")
        UUID creatorId,

        @JsonProperty("topic_id")
        UUID topicId,

        @JsonProperty("category_id")
        UUID categoryId
) {
}
