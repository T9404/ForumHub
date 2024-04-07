package com.example.core.jwt.refresh;

import com.example.core.jwt.refresh.entity.RefreshTokenEntity;
import lombok.NonNull;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private static final String KEY = "Refresh";

    private final HashOperations<String, String, RefreshTokenEntity> hashOperations;

    public RefreshTokenRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void saveRefreshToken(@NonNull RefreshTokenEntity entity) {
        hashOperations.put(KEY, entity.getTokenId(), entity);
    }

    @Override
    public Optional<RefreshTokenEntity> getRefreshTokenById(@NonNull String tokenId) {
        return Optional.ofNullable(hashOperations.get(KEY, tokenId));
    }

    @Override
    public void deleteRefreshToken(@NonNull String tokenId) {
        hashOperations.delete(KEY, tokenId);
    }
}

