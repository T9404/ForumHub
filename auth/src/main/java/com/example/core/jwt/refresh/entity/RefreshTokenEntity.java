package com.example.core.jwt.refresh.entity;

import lombok.*;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity implements Serializable {
    private String tokenId;
    private String refreshToken;
    @TimeToLive
    private long expiration;
}
