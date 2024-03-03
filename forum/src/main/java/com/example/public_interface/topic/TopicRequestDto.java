package com.example.public_interface.topic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record TopicRequestDto(
        String name,

        @JsonProperty("category_id")
        UUID categoryId,

        @JsonProperty("author_id")
        UUID authorId
) {
}
