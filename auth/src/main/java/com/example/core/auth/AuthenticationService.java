package com.example.core.auth;

import com.example.contract.notification.DeliveryChannel;
import com.example.contract.notification.NotificationDto;
import com.example.core.auth.mail.property.MailMessageProperty;
import com.example.core.confirmation.ConfirmationService;
import com.example.core.jwt.dto.TokenGenerationData;
import com.example.core.auth.event.AuthEvent;
import com.example.core.user.role.role.RoleEntity;
import com.example.core.user.role.role.RoleId;
import com.example.exception.event.UserEvent;
import com.example.core.user.repository.entity.UserEntity;
import com.example.core.user.UserService;
import com.example.exception.BusinessException;
import com.example.rest.admin.v1.request.CreateUserDto;
import com.example.rest.auth.v1.request.LoginRequest;
import com.example.rest.auth.v1.request.RefreshTokenRequest;
import com.example.rest.auth.v1.request.ResendTokenRequest;
import com.example.rest.auth.v1.response.JwtResponse;
import com.example.rest.auth.v1.response.RegistrationResponse;
import com.example.security.dto.role.Role;
import com.example.security.enums.RoleType;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final MailMessageProperty mailMessageProperty;
    private final ConfirmationService confirmationService;
    private final TokenService tokenService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserService userService;

    @Value("${exporter.notification.source-topic}")
    private String topicName;

    @Transactional
    public RegistrationResponse signUp(@NonNull CreateUserDto dto) {
        checkIfUsernameExists(dto.username());
        checkIfEmailExists(dto.email());

        var user = userService.create(dto);
        sendConfirmationMail(user);
        return new RegistrationResponse("Verification email has been sent to " + user.getEmail());
    }

    @Transactional
    public void confirmEmail(String token) {
        var confirmationToken = confirmationService.getConfirmationToken(token);
        confirmationService.validateConfirmationToken(confirmationToken);

        userService.confirmEmail(confirmationToken);
        confirmationService.confirmToken(confirmationToken);
    }

    @Transactional
    public void resendToken(ResendTokenRequest token) {
        var user = userService.getByEmail(token.email());

        var role = RoleEntity.builder()
                .id(new RoleId(user.getUserId(), RoleType.UNVERIFIED.name()))
                .build();

        if (user.getRoles().contains(role)) {
            sendConfirmationMail(user);
        }

        throw new BusinessException(UserEvent.USER_ALREADY_VERIFIED, "User is already verified");
    }

    public JwtResponse signIn(@NonNull LoginRequest request) {
        var user = userService.getByEmail(request.email());

        checkPassword(request, user);
        checkIfUserVerified(user);

        var userData = new TokenGenerationData(user.getUserId(), user.getRoles());
        return tokenService.createTokens(userData);
    }

    public JwtResponse refresh(@NonNull RefreshTokenRequest request) {
        tokenService.checkRefreshToken(request.refreshToken());

        final String tokenId = tokenService.getTokenIdInRefreshToken(request.refreshToken());
        tokenService.deleteRefreshToken(tokenId);

        final String userId = tokenService.getUserIdInRefreshToken(request.refreshToken());
        final UserEntity user = userService.getById(userId);
        var tokenGenerationData = new TokenGenerationData(user.getUserId(), user.getRoles());

        return tokenService.createTokens(tokenGenerationData);
    }

    public void logout(@NonNull RefreshTokenRequest request) {
        tokenService.checkRefreshToken(request.refreshToken());
        final String tokenId = tokenService.getTokenIdInRefreshToken(request.refreshToken());
        tokenService.deleteRefreshToken(tokenId);
    }

    private void checkIfUsernameExists(String username) {
        if (userService.existsByUsername(username)) {
            throw new BusinessException(UserEvent.USER_ALREADY_EXISTS, "Username already exists");
        }
    }

    private void checkIfEmailExists(String email) {
        if (userService.existsByEmail(email)) {
            throw new BusinessException(UserEvent.USER_ALREADY_EXISTS, "Email already exists");
        }
    }

    private void sendConfirmationMail(UserEntity user) {
        var confirmationToken = confirmationService.createConfirmationToken(user);
        sendConfirmationToken(user, confirmationToken);
    }

    private void sendConfirmationToken(UserEntity user, String confirmationToken) {
        String content = String.format(mailMessageProperty.getContent(), confirmationToken);
        var notification = buildNotificationDto(user, mailMessageProperty.getSubject(), content);
        kafkaTemplate.send(topicName, notification);
    }

    private NotificationDto buildNotificationDto(UserEntity user, String title, String content) {
        return new NotificationDto(
                UUID.randomUUID().toString(),
                title,
                content,
                user.getUserId().toString(),
                OffsetDateTime.now().toEpochSecond(),
                false,
                List.of(DeliveryChannel.EMAIL)
        );
    }

    private void checkPassword(LoginRequest request, UserEntity user) {
        if (!userService.checkPassword(request.password(), user.getPassword())) {
            throw new BusinessException(AuthEvent.INVALID_CREDENTIALS, "Email or password is incorrect");
        }
    }

    private void checkIfUserVerified(UserEntity user) {
        var isUserVerified = userService.isUserVerified(user);
        if (!isUserVerified) {
            throw new BusinessException(UserEvent.USER_NOT_VERIFIED, "User is not verified");
        }
    }

}
