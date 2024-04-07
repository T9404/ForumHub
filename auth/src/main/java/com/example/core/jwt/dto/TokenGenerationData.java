package com.example.core.jwt.dto;

import com.example.core.user.role.role.RoleEntity;

import java.util.Set;
import java.util.UUID;

public record TokenGenerationData(
        UUID userId,
        Set<RoleEntity> role
) {
}
