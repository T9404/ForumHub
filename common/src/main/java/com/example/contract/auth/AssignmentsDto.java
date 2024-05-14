package com.example.contract.auth;

import java.util.Set;
import java.util.UUID;

public record AssignmentsDto(
        Set<UUID> categories
) {
}
