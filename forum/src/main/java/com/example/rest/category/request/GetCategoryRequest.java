package com.example.rest.category.request;

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
