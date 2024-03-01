package com.example.public_interface.topic;

import java.util.UUID;

public record TopicRequestDto(
        String name,
        UUID categoryId,
        UUID authorId
) {
}
