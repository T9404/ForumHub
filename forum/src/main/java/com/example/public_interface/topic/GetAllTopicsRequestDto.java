package com.example.public_interface.topic;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetAllTopicsRequestDto(
        @JsonProperty("current_page")
        Integer page,

        @JsonProperty("page_size")
        Integer size,

        String orderSortingType,

        String topicSorting
) {
}
