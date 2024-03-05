package com.example.public_interface.category;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateCategoryRequestDto(
        @JsonProperty("previous_category_id")
        UUID previousCategoryId,

        @JsonProperty("name")
        String name,

        @JsonProperty("creator_id")
        UUID creatorId
) {
}
