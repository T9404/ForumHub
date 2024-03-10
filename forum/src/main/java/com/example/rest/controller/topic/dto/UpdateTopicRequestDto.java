package com.example.rest.controller.topic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UpdateTopicRequestDto(
        @JsonProperty("topic_id")
        UUID topicId,

        String name,

        @JsonProperty("category_id")
        UUID categoryId
) {
}
