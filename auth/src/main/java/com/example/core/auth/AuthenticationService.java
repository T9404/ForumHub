package com.example.core.auth;

import com.example.core.confirmation.ConfirmationService;
import com.example.core.jwt.dto.TokenGenerationData;
import com.example.core.auth.event.AuthEvent;
import com.example.exception.event.UserEvent;
import com.example.core.mail.MailService;
import com.example.core.mail.property.MailMessageProperty;
import com.example.core.user.repository.entity.UserEntity;
import com.example.core.user.UserService;
import com.example.exception.BusinessException;
import com.example.rest.admin.v1.request.CreateUserDto;
import com.example.rest.auth.v1.request.LoginRequest;
import com.example.rest.auth.v1.request.RefreshTokenRequest;
import com.example.rest.auth.v1.request.ResendTokenRequest;
import com.example.rest.auth.v1.response.JwtResponse;
import com.example.rest.auth.v1.response.RegistrationResponse;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final MailMessageProperty mailMessageProperty;
    private final ConfirmationService confirmationService;
    private final TokenService tokenService;
    private final MailService mailService;
    private final UserService userService;

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
        sendConfirmationMail(user);
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
        sendConfirmationToken(confirmationToken, user.getEmail());
    }

    private void sendConfirmationToken(String confirmationToken, String email) {
        String content = String.format(mailMessageProperty.getContent(), confirmationToken);
        mailService.sendMail(email, mailMessageProperty.getSubject(), content);
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
