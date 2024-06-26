package com.example.category.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCategoryRequestDto(
        @JsonProperty("previous_category_id")
        UUID previousCategoryId,

        @JsonProperty("name")
        String name
) {
}
