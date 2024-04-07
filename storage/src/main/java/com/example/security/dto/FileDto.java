package com.example.security.dto;

import org.springframework.context.annotation.Description;

import java.sql.Timestamp;
import java.util.UUID;

public record FileDto(
        UUID fileId,
        String name,

        @Description("Size in bytes")
        long size,

        Timestamp uploadTime
) {
}
