package com.example.public_interface.user;

public record CreateUserDto(
        String username,
        String password,
        String email,
        String fullName,
        String phoneNumber
) {
}
