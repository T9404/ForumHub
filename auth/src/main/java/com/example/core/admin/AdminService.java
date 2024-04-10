package com.example.core.admin;

import com.example.contract.AssignmentsDto;
import com.example.core.assignment.AssignmentService;
import com.example.core.user.UserService;
import com.example.core.user.repository.entity.UserEntity;
import com.example.security.enums.RoleType;
import com.example.rest.admin.v1.request.CreateUserDto;
import com.example.rest.admin.v1.response.UserDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AssignmentService assignmentService;
    private final UserService userService;

    public UserDto getUser(String userId) {
        var user = userService.getById(userId);
        return mapUserToDtoWithRoles(user);
    }

    public List<UserDto> getAllUsers() {
        return userService.getAll().stream()
                .map(this::mapUserToDtoWithRoles)
                .toList();
    }

    @Transactional
    public UserDto createUser(CreateUserDto dto) {
        var user = userService.createUserByAdmin(dto);
        userService.addRole(user, dto.roleType());
        return mapUserToDtoWithRoles(user);
    }

    @Transactional
    public UserDto updateUser(String userId, CreateUserDto dto) {
        var user = userService.update(userId, dto);
        return mapUserToDtoWithRoles(user);
    }

    @Transactional
    public void blockUser(String userId) {
        userService.blockUser(userId);
    }

    @Transactional
    public void unblockUser(String userId) {
        userService.unblockUser(userId);
    }

    @Transactional
    public void assignCategory(String userId, String categoryId) {
        var user = userService.getById(userId);
        userService.updateRole(user, RoleType.MODERATOR);
        assignmentService.assignCategory(userId, categoryId);
    }

    @Transactional
    public void unassignCategory(String userId, String categoryId) {
        var user = userService.getById(userId);
        assignmentService.removeAssignment(userId, categoryId);

        if (assignmentService.hasNoneAssignment(userId)) {
            userService.updateRole(user, RoleType.USER);
        }
    }

    public AssignmentsDto getAssignment(String userId) {
        var user = userService.getById(userId);
        var assignedCategories = assignmentService.findAssignedCategories(user.getUserId());
        return new AssignmentsDto(assignedCategories);
    }


    private UserDto mapUserToDtoWithRoles(UserEntity user) {
        Set<String> roles = getAllRoles(user.getUserId());
        return mapToDto(user, roles);
    }

    private Set<String> getAllRoles(UUID userId) {
        return userService.getAllRoles(userId).stream()
                .map(role -> role.getId().getRole())
                .collect(Collectors.toSet());
    }

    private UserDto mapToDto(UserEntity user, Set<String> roles) {
        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                roles
        );
    }
}
