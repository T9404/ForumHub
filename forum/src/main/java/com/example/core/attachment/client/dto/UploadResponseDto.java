package com.example.core.attachment.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UploadResponseDto(
        @JsonProperty("file_id")
        UUID fileId
) {
}
