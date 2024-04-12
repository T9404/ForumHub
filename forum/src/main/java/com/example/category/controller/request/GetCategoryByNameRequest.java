package com.example.category.controller.request;

import lombok.Builder;

@Builder
public record GetCategoryByNameRequest(
        String name
) {
}
