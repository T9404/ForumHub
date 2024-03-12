package com.example.public_interface.category;

import lombok.Builder;

@Builder
public record GetCategoryByNameDto(
        String name
) {
}
