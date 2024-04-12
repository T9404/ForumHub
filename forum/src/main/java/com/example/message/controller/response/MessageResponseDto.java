package com.example.message.controller.response;

import com.example.attachment.dto.AttachmentDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record MessageResponseDto(
        @JsonProperty("message_id")
        UUID messageId,

        String content,

        @JsonProperty("created_at")
        OffsetDateTime createdAt,

        @JsonProperty("modification_at")
        OffsetDateTime modificationAt,

        @JsonProperty("creator_id")
        UUID creatorId,

        Set<AttachmentDto> attachments
) {
}
