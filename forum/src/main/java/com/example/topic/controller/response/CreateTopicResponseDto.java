package com.example.topic.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateTopicResponseDto(
        @JsonProperty("topic_id")
        UUID topicId
) {
}
