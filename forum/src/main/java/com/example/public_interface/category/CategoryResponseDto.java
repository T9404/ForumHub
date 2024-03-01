package com.example.public_interface.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record CategoryResponseDto(
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
        UUID creatorId
) {
}
