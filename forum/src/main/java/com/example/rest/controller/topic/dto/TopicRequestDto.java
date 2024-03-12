package com.example.rest.controller.topic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TopicRequestDto(
        String name,

        @JsonProperty("category_id")
        UUID categoryId,

        @JsonProperty("author_id")
        UUID authorId
) {
}
