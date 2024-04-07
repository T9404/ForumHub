package com.example.public_interface.admin;

import com.example.core.user.role.enums.RoleType;

public record UpdateUserDto(
        String username,
        String email,
        String password,
        String fullName,
        String phoneNumber,
        RoleType roleType
) {
}
