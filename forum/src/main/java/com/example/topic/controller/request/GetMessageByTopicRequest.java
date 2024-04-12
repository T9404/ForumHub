package com.example.topic.controller.request;

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
