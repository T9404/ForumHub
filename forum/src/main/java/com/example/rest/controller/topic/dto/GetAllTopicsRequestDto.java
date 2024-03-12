package com.example.rest.controller.topic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record GetAllTopicsRequestDto(
        @JsonProperty("current_page")
        Integer page,

        @JsonProperty("page_size")
        Integer size,

        @JsonProperty("order_sorting_type")
        String orderSortingType,

        @JsonProperty("topic_sorting")
        String topicSorting
) {
}
