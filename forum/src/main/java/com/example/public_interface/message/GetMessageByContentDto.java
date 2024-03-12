package com.example.public_interface.message;

import lombok.Builder;

@Builder
public record GetMessageByContentDto(
        String content
) {
}
