package com.example.rest.controller.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record GetCategoryRequest(
        @JsonProperty("topic_sorting")
        String topicSorting,

        @JsonProperty("topic_direction")
        String topicDirection
) {
}
