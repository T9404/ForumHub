package com.example.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UploadResponseDto(
        @JsonProperty("file_id")
        UUID fileId
) {
}
