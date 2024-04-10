package com.example.security.jwt;

import com.example.exception.BusinessException;
import com.example.exception.event.JwtTokenEvent;
import com.example.security.dto.role.Role;
import com.example.security.dto.role.RoleId;
import com.example.security.jwt.property.JwtTokenProperty;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

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

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public String extractUserIdFromAccessToken(@NonNull String accessToken) {
        return getAccessClaims(accessToken).getSubject();
    }

    public Set<Role> extractRoleFromAccessToken(@NonNull String accessToken) {
        try {
            Claims claims = getAccessClaims(accessToken);
            LinkedHashMap<String, Object> values = getRoleClaim(claims);
            Role roleEntity = buildRoleEntity(values);
            return Collections.singleton(roleEntity);
        } catch (Exception e) {
            throw new BusinessException(JwtTokenEvent.INVALID_TOKEN, "Invalid token");
        }
    }

    public boolean isAccessTokenValid(@NonNull String accessToken) {
        validateToken(accessToken, jwtAccessSecret);
        getAccessClaims(accessToken);
        return true;
    }

    private Claims getClaims(@NonNull String token, @NonNull SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static void validateToken(@NonNull String token, @NonNull SecretKey secret) {
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

    private LinkedHashMap<String, Object> getRoleClaim(Claims claims) {
        Object roleClaimObj = claims.get(ROLE);
        if (roleClaimObj instanceof List<?> roleClaim && (!roleClaim.isEmpty()
                && roleClaim.getFirst() instanceof LinkedHashMap<?, ?> firstRoleClaim)) {
            Object idObj = firstRoleClaim.get("id");
            if (idObj instanceof LinkedHashMap) {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> idMap = (LinkedHashMap<String, Object>) idObj;
                return idMap;
            }
        }
        throw new IllegalArgumentException("Invalid role claim format");
    }

    private Role buildRoleEntity(LinkedHashMap<String, Object> values) {
        var role = (String) values.get("role");
        var userId = (String) values.get("userId");
        return new Role(
                new RoleId(UUID.fromString(userId), role)
        );
    }
}
