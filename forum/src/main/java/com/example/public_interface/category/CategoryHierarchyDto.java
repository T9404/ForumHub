package com.example.public_interface.category;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CategoryHierarchyDto(

        @JsonProperty("category_id")
        UUID categoryId,

        @JsonProperty("previous_category_id")
        UUID previousCategoryId,

        String name,

        @JsonProperty("created_at")
        OffsetDateTime createdAt,

        @JsonProperty("modification_at")
        OffsetDateTime modificationAt,

        @JsonProperty("creator_id")
        UUID creatorId,

        List<CategoryHierarchyDto> children
) {
}
