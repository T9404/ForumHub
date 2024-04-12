package com.example.attachment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AttachmentDto(
        @JsonProperty("attachment_id")
        UUID attachmentId,

        @JsonProperty("file_id")
        UUID fileId
) {
}
