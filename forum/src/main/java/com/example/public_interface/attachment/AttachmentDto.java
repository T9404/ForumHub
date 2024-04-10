package com.example.public_interface.attachment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AttachmentDto(
        @JsonProperty("attachment_id")
        UUID attachmentId,

        @JsonProperty("file_id")
        UUID fileId
) {
}
