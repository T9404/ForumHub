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
        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(user);
        confirmationToken = confirmationTokenRepository.save(confirmationToken);
        return confirmationToken.getToken();
    }

    public void confirmToken(ConfirmationTokenEntity confirmationToken) {
        confirmationToken.setConfirmed(true);
        confirmationTokenRepository.save(confirmationToken);
    }

    public ConfirmationTokenEntity getConfirmationToken(String token) {
        return confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ConfirmationTokenEvent.TOKEN_NOT_FOUND, ERROR_CONFIRMATION_MESSAGE));
    }

    public void validateConfirmationToken(ConfirmationTokenEntity confirmationToken) {
        if (confirmationToken.isConfirmed()) {
            throw new BusinessException(ConfirmationTokenEvent.TOKEN_ALREADY_CONFIRMED, "Token already confirmed");
        }

        var maxPossibleExpiration = confirmationToken.getCreatedAt().plusSeconds(tokenExpiration);
        if (maxPossibleExpiration.isBefore(OffsetDateTime.now())) {
            throw new BusinessException(ConfirmationTokenEvent.TOKEN_EXPIRED, "Token expired");
        }
    }
}
