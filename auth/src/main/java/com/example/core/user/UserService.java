package com.example.core.user;

import com.example.exception.event.UserEvent;
import com.example.core.confirmation.repository.entity.ConfirmationTokenEntity;
import com.example.rest.admin.v1.request.CreateUserDto;
import com.example.security.enums.RoleType;
import com.example.core.user.role.RoleRepository;
import com.example.core.user.repository.UserRepository;
import com.example.core.user.repository.entity.UserEntity;
import com.example.core.user.role.role.RoleEntity;
import com.example.core.user.role.role.RoleId;
import com.example.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserEntity create(CreateUserDto request) {
        return createUser(request, RoleType.UNVERIFIED);
    }

    public UserEntity createUserByAdmin(CreateUserDto request) {
        return createUser(request, request.roleType());
    }

    public boolean isUserVerified(UserEntity user) {
        return !hasRole(user, RoleType.UNVERIFIED);
    }

    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    public UserEntity update(String userId, CreateUserDto dto) {
        var user = getById(userId);

        if (StringUtils.isNotBlank(dto.username())) {
            user.setUsername(dto.username());
        }

        if (StringUtils.isNotBlank(dto.email())) {
            user.setEmail(dto.email());
        }

        if (StringUtils.isNotBlank(dto.fullName())) {
            user.setFullName(dto.fullName());
        }

        if (StringUtils.isNotBlank(dto.phoneNumber())) {
            user.setPhoneNumber(dto.phoneNumber());
        }

        if (StringUtils.isNotBlank(dto.password())) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        updateRole(user, dto.roleType());
        return userRepository.save(user);
    }

    public void updateRole(UserEntity user, RoleType roleType) {
        roleRepository.updateRole(user.getUserId(), roleType.name());
    }

    public Set<RoleEntity> getAllRoles(UUID userId) {
        return roleRepository.findAllByIdUserId(userId);
    }

    public void blockUser(String userId) {
        var user = getById(userId);

        if (hasRole(user, RoleType.ADMIN)) {
            throw new BusinessException(UserEvent.USER_NOT_BLOCKED, "Admin cannot be blocked");
        }
        if (hasRole(user, RoleType.BLOCKED)) {
            throw new BusinessException(UserEvent.USER_ALREADY_BLOCKED, "User is already blocked");
        }

        updateRole(user, RoleType.BLOCKED);
    }

    public void unblockUser(String userId) {
        var user = getById(userId);

        if (!hasRole(user, RoleType.BLOCKED)) {
            throw new BusinessException(UserEvent.USER_NOT_BLOCKED, "User is not blocked");
        }

        updateRole(user, RoleType.USER);
    }

    public void addRole(UserEntity user, RoleType roleType) {
        var role = RoleEntity.builder()
                .id(new RoleId(user.getUserId(), roleType.name()))
                .build();

        user.getRoles().add(role);
        userRepository.save(user);
        roleRepository.save(role);
    }

    public void confirmEmail(ConfirmationTokenEntity confirmationToken) {
        var user = confirmationToken.getUser();
        addRole(user, RoleType.USER);
        roleRepository.deleteAllByIdUserIdAndIdRole(user.getUserId(), RoleType.UNVERIFIED.name());
    }

    public boolean checkPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserEntity getById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(UserEvent.USER_NOT_FOUND, "User not found"));
    }

    public UserEntity getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(UserEvent.USER_NOT_FOUND, "User not found"));
    }

    private UserEntity createUser(CreateUserDto request, RoleType roleType) {
        checkIfUsernameExists(request.username());
        checkIfEmailExists(request.email());

        var user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .roles(new HashSet<>())
                .build();

        var savedUser = userRepository.save(user);
        addRole(savedUser, roleType);
        return savedUser;
    }

    public boolean hasRole(UserEntity user, RoleType roleType) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getId().getRole().equals(roleType.name()));
    }

    private void checkIfUsernameExists(String username) {
        if (existsByUsername(username)) {
            throw new BusinessException(UserEvent.USER_ALREADY_EXISTS, "Username already exists");
        }
    }

    private void checkIfEmailExists(String email) {
        if (existsByEmail(email)) {
            throw new BusinessException(UserEvent.USER_ALREADY_EXISTS, "Email already exists");
        }
    }
}
