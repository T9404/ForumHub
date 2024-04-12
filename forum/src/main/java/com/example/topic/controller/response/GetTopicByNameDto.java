package com.example.topic.controller.response;

import lombok.Builder;

@Builder
public record GetTopicByNameDto(
        String name
) {
}
