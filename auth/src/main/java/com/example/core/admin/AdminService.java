package com.example.core.admin;

import com.example.core.user.UserService;
import com.example.public_interface.admin.UpdateUserDto;
import com.example.public_interface.user.CreateUserDto;
import com.example.rest.admin.v1.request.CreateUserByAdminRequest;
import com.example.rest.admin.v1.response.UserDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserService userService;

    public UserDto getUser(String userId) {
        var user = userService.getById(userId);

        var roles = user.getRoles().stream()
                .map(role -> role.getId().getRole())
                .toList();

        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                roles
        );
    }

    @Transactional
    public UserDto createUser(CreateUserByAdminRequest request) {
        var dto = new CreateUserDto(
                request.username(),
                request.password(),
                request.email(),
                request.fullName(),
                request.phoneNumber()
        );
        var user = userService.createUserByAdmin(dto, request.roleType());

        userService.addRole(user, request.roleType());

        var roles = userService.getAllRoles(user.getUserId()).stream()
                .map(role -> role.getId().getRole())
                .toList();

        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                roles
        );
    }

    @Transactional
    public UserDto updateUser(String userId, UpdateUserDto dto) {
        var user = userService.update(userId, dto);

        var roles = userService.getAllRoles(UUID.fromString(userId)).stream()
                .map(role -> role.getId().getRole())
                .toList();

        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                roles
        );
    }

    @Transactional
    public void blockUser(String userId) {
        userService.blockUser(userId);
    }

    @Transactional
    public void unblockUser(String userId) {
        userService.unblockUser(userId);
    }

}
