package com.example.topic.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TopicRequestDto(
        String name,

        @JsonProperty("category_id")
        UUID categoryId
) {
}
