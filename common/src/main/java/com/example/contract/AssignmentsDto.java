package com.example.contract;

import java.util.Set;
import java.util.UUID;

public record AssignmentsDto(
        Set<UUID> categories
) {
}
