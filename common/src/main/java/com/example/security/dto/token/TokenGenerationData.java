package com.example.security.dto.token;

import com.example.security.dto.role.Role;

import java.util.Set;
import java.util.UUID;

public record TokenGenerationData(
        UUID userId,
        Set<Role> role
) {
}
