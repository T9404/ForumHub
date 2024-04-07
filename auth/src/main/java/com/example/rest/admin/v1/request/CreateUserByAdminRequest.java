package com.example.rest.admin.v1.request;

import com.example.core.user.role.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserByAdminRequest(
        @NotBlank(message = "Invalid username: EMPTY username")
        @NotNull(message = "Invalid username: username is NULL")
        @Size(min = 3, max = 50, message = "Username should be between 3 and 50 characters")
        String username,

        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Invalid password: EMPTY password")
        @NotNull(message = "Invalid password: password is NULL")
        @Size(min = 5, max = 50, message = "Password should be between 5 and 50 characters")
        String password,

        @NotBlank(message = "Invalid fullName: EMPTY fullName")
        @NotNull(message = "Invalid fullName: fullName is NULL")
        @JsonProperty("full_name")
        String fullName,

        @NotBlank(message = "Invalid fullName: EMPTY fullName")
        @NotNull(message = "Invalid fullName: fullName is NULL")
        @JsonProperty("phone_number")
        String phoneNumber,

        @JsonProperty("role")
        RoleType roleType
) {
}
