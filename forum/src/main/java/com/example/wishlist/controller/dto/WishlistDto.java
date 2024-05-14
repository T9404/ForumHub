package com.example.wishlist.controller.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WishlistDto(
        UUID userId,
        UUID topicId,
        OffsetDateTime createdAt
) {
}
