package com.example.public_interface.topic;

import lombok.Builder;

@Builder
public record GetTopicByNameDto(
        String name
) {
}
