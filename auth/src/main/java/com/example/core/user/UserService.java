package com.example.core.user;

import com.example.core.user.event.UserEvent;
import com.example.core.confirmation.repository.entity.ConfirmationTokenEntity;
import com.example.core.user.role.enums.RoleType;
import com.example.core.user.role.RoleRepository;
import com.example.core.user.repository.UserRepository;
import com.example.core.user.repository.entity.UserEntity;
import com.example.core.user.role.role.RoleEntity;
import com.example.core.user.role.role.RoleId;
import com.example.exception.BusinessException;
import com.example.public_interface.admin.UpdateUserDto;
import com.example.public_interface.user.CreateUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

    public UserEntity createUserByAdmin(CreateUserDto request, RoleType roleType) {
        return createUser(request, roleType);
    }

    public boolean isUserVerified(UserEntity user) {
        return user.getRoles().stream()
                .noneMatch(role -> role.getId().getRole().equals(RoleType.UNVERIFIED.name()));
    }

    public UserEntity update(String userId, UpdateUserDto dto) {
        var user = getById(userId);
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setFullName(dto.fullName());
        user.setPhoneNumber(dto.phoneNumber());
        user.setPassword(passwordEncoder.encode(dto.password()));

        roleRepository.updateRole(user.getUserId(), dto.roleType().name());
        return userRepository.save(user);
    }

    public Set<RoleEntity> getAllRoles(UUID userId) {
        return roleRepository.findAllByIdUserId(userId);
    }

    public void blockUser(String userId) {
        var user = getById(userId);

        if (hasRole(user, RoleType.BLOCKED)) {
            throw new BusinessException(UserEvent.USER_ALREADY_BLOCKED, "User is already blocked");
        }

        roleRepository.updateRole(user.getUserId(), RoleType.BLOCKED.name());
    }

    public void unblockUser(String userId) {
        var user = getById(userId);

        if (!hasRole(user, RoleType.BLOCKED)) {
            throw new BusinessException(UserEvent.USER_NOT_BLOCKED, "User is not blocked");
        }

        roleRepository.updateRole(user.getUserId(), RoleType.USER.name());
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

        var unverifiedRole = RoleEntity.builder()
                .id(new RoleId(user.getUserId(), RoleType.UNVERIFIED.name()))
                .build();
        roleRepository.delete(unverifiedRole);
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
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDetailsService userDetailsService() {
        return this::getById;
    }

    public UserEntity getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
