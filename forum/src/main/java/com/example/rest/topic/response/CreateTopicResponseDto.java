package com.example.rest.topic.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateTopicResponseDto(
        @JsonProperty("topic_id")
        UUID topicId
) {
}
