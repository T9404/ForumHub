package com.example.message.controller.response;

import lombok.Builder;

@Builder
public record GetMessageByContentDto(
        String content
) {
}
