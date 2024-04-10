package com.example.core.auth;

import com.example.exception.event.JwtTokenEvent;
import com.example.core.jwt.JwtProvider;
import com.example.core.jwt.dto.TokenGenerationData;
import com.example.core.jwt.refresh.entity.RefreshTokenEntity;
import com.example.core.jwt.refresh.RefreshTokenRepository;
import com.example.exception.BusinessException;
import com.example.rest.auth.v1.response.JwtResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public JwtResponse createTokens(TokenGenerationData data) {
        final String accessToken = jwtProvider.generateAccessToken(data);
        final String refreshToken = jwtProvider.generateRefreshToken(data);

        final RefreshTokenEntity refreshTokenEntity = getRefreshTokenEntity(refreshToken);
        refreshTokenRepository.saveRefreshToken(refreshTokenEntity);

        return new JwtResponse(accessToken, refreshToken);
    }

    public void checkRefreshToken(String refreshToken) {
        jwtProvider.validateRefreshToken(refreshToken);

        final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        final String tokenId = claims.getId();

        final RefreshTokenEntity oldRefreshToken = refreshTokenRepository.getRefreshTokenById(tokenId)
                .orElseThrow(() -> new BusinessException(JwtTokenEvent.TOKEN_NOT_FOUND, "Refresh token not found"));

        if (!oldRefreshToken.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(JwtTokenEvent.INVALID_TOKEN, "Invalid refresh token");
        }
    }

    public String getUserIdInRefreshToken(String refreshToken) {
        return jwtProvider.getRefreshClaims(refreshToken).getSubject();
    }

    public String getTokenIdInRefreshToken(String refreshToken) {
        return jwtProvider.getRefreshClaims(refreshToken).getId();
    }

    public void deleteRefreshToken(String tokenId) {
        refreshTokenRepository.deleteRefreshToken(tokenId);
    }

    private RefreshTokenEntity getRefreshTokenEntity(String refreshToken) {
        return new RefreshTokenEntity(
                jwtProvider.getRefreshClaims(refreshToken).getId(),
                refreshToken,
                jwtProvider.getRefreshTokenTtl()
        );
    }
}
