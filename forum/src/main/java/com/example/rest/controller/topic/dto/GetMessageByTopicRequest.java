package com.example.rest.controller.topic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GetMessageByTopicRequest(
        @JsonProperty("topic_id")
        UUID topicId,


        String direction,

        @JsonProperty("sort_by")
        String sortBy
) {
}
