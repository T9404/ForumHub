package com.example.rest.topic.response;

import lombok.Builder;

@Builder
public record GetTopicByNameDto(
        String name
) {
}
