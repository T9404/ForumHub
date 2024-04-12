package com.example.category.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateCategoryResponseDto(
        @JsonProperty("category_id")
        UUID categoryId
) {
}
