package com.example.security.dto;

import org.springframework.core.io.ByteArrayResource;

public record DownloadFileDto(
        ByteArrayResource file,
        String fileName
) {
}
