package com.example.rest.topic.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateTopicRequestDto(
        @JsonProperty("topic_id")
        UUID topicId,

        String name,

        @JsonProperty("category_id")
        UUID categoryId
) {
}
