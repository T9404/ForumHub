package com.example.rest.category.request;

import lombok.Builder;

@Builder
public record GetCategoryByNameRequest(
        String name
) {
}
