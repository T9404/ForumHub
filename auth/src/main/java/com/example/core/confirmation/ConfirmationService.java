package com.example.core.confirmation;

import com.example.core.confirmation.event.ConfirmationTokenEvent;
import com.example.core.confirmation.repository.ConfirmationTokenRepository;
import com.example.core.confirmation.repository.entity.ConfirmationTokenEntity;
import com.example.core.user.repository.entity.UserEntity;
import com.example.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmationService {
    private static final String ERROR_CONFIRMATION_MESSAGE = "Invalid confirmation token";
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Value("${verification.token.expiration}")
    private long tokenExpiration;

    public String createConfirmationToken(UserEntity user) {
        var token = new ConfirmationTokenEntity(user);
        token = confirmationTokenRepository.save(token);
        return token.getToken();
    }

    public void confirmToken(ConfirmationTokenEntity token) {
        token.setConfirmed(true);
        confirmationTokenRepository.save(token);
    }

    public ConfirmationTokenEntity getConfirmationToken(String token) {
        return confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ConfirmationTokenEvent.TOKEN_NOT_FOUND, ERROR_CONFIRMATION_MESSAGE));
    }

    public void validateConfirmationToken(ConfirmationTokenEntity confirmationToken) {
        if (confirmationToken.isConfirmed()) {
            throw new BusinessException(ConfirmationTokenEvent.TOKEN_ALREADY_CONFIRMED, "Token already confirmed");
        }

        var maxExpiration = confirmationToken.getCreatedAt().plusSeconds(tokenExpiration);
        if (maxExpiration.isBefore(OffsetDateTime.now())) {
            throw new BusinessException(ConfirmationTokenEvent.TOKEN_EXPIRED, "Token expired");
        }
    }
}
