package com.example.public_interface.category;

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
