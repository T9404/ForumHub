package com.example.security.util;

import com.example.security.dto.user.User;
import com.example.security.enums.RoleType;

public final class RoleUtil {

    private RoleUtil() {
        throw new UnsupportedOperationException();
    }

    public static boolean hasRole(User user, RoleType roleType) {
        var roles = user.getRoles();
        return roles.stream()
                .anyMatch(role -> role.roleId().getRole().equals(roleType.name()));
    }
}
