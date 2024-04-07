package com.example.core.jwt.refresh;

import com.example.core.jwt.refresh.entity.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshTokenRepository {
    void saveRefreshToken(RefreshTokenEntity entity);
    Optional<RefreshTokenEntity> getRefreshTokenById(String tokenId);
    void deleteRefreshToken(String tokenId);
}

