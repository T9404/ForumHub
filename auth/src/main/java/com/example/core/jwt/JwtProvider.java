package com.example.core.jwt;

import com.example.core.jwt.event.JwtTokenEvent;
import com.example.core.jwt.dto.TokenGenerationData;
import com.example.core.jwt.property.JwtTokenProperty;
import com.example.core.user.repository.entity.UserEntity;
import com.example.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Getter
@Service
public class JwtProvider {
    private static final String ROLE = "role";
    private final SecretKey jwtAccessSecret;
    private final long jwtAccessTtlSecond;
    private final SecretKey jwtRefreshSecret;
    private final long jwtRefreshTtlSecond;

    public JwtProvider(JwtTokenProperty jwtTokenProperty) {
        jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtTokenProperty.getAccess().secret()));
        jwtAccessTtlSecond = jwtTokenProperty.getAccess().expiration();
        jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtTokenProperty.getRefresh().secret()));
        jwtRefreshTtlSecond = jwtTokenProperty.getRefresh().expiration();
    }

    public String generateAccessToken(@NonNull TokenGenerationData data) {
        final Date accessExpiration = getDateWithPlus(jwtAccessTtlSecond);
        final String accessTokenId = UUID.randomUUID().toString();
        return Jwts.builder()
                .subject(String.valueOf(data.userId()))
                .claim(ROLE, data.role())
                .expiration(accessExpiration)
                .id(accessTokenId)
                .signWith(jwtAccessSecret)
                .compact();
    }

    public String generateRefreshToken(@NonNull TokenGenerationData data) {
        final Date refreshExpiration = getDateWithPlus(jwtRefreshTtlSecond);
        final String refreshTokenId = UUID.randomUUID().toString();
        return Jwts.builder()
                .subject(String.valueOf(data.userId()))
                .expiration(refreshExpiration)
                .id(refreshTokenId)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public void validateRefreshToken(@NonNull String refreshToken) {
        validateToken(refreshToken, jwtRefreshSecret);
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    public long getRefreshTokenTtl() {
        return jwtRefreshTtlSecond;
    }

    public String extractUserIdFromAccessToken(@NonNull String accessToken) {
        return getAccessClaims(accessToken).getSubject();
    }

    public boolean isAccessTokenValid(@NonNull String accessToken, UserEntity user) {
        validateToken(accessToken, jwtAccessSecret);
        var claims = getAccessClaims(accessToken);
        return user.getUserId().toString().equals(claims.getSubject());
    }

    private Claims getClaims(@NonNull String token, @NonNull SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void validateToken(@NonNull String token, @NonNull SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parse(token);
        } catch (ExpiredJwtException expEx) {
            throw new BusinessException(JwtTokenEvent.EXPIRED_TOKEN, "Token expired");
        } catch (UnsupportedJwtException unsEx) {
            throw new BusinessException(JwtTokenEvent.UNSUPPORTED_TOKEN, "Unsupported token");
        } catch (MalformedJwtException mjEx) {
            throw new BusinessException(JwtTokenEvent.MALFORMED_TOKEN, "Malformed token");
        } catch (Exception e) {
            throw new BusinessException(JwtTokenEvent.INVALID_TOKEN, "Invalid token");
        }
    }

    private Date getDateWithPlus(long delta) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusSeconds(delta)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        return Date.from(refreshExpirationInstant);
    }

}
