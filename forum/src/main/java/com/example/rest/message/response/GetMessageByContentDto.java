package com.example.rest.message.response;

import lombok.Builder;

@Builder
public record GetMessageByContentDto(
        String content
) {
}
