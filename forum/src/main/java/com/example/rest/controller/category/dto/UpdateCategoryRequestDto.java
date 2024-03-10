package com.example.rest.controller.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UpdateCategoryRequestDto(
        @JsonProperty("category_id")
        UUID categoryId,

        @JsonProperty("previous_category_id")
        UUID previousCategoryId,

        String name
) {
}
