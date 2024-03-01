package com.example.public_interface.topic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TopicResponseDto(
        @JsonProperty("topic_id")
        UUID topicId,

        String name,

        @JsonProperty("category_id")
        UUID categoryId,

        @JsonProperty("created_at")
        OffsetDateTime createdAt,

        @JsonProperty("modification_at")
        OffsetDateTime modificationAt,

        @JsonProperty("creator_id")
        UUID creatorId
) {
}
