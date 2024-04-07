package com.example.security.jwt;

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
            var claims = getAccessClaims(accessToken);
            System.out.println(claims.get(ROLE));
            Set<Role> roles = new HashSet<>();
            var role = new Role(new RoleId(UUID.fromString(claims.getSubject()), "ADMIN"));
            roles.add(role);
            return roles;
        } catch (Exception e) {
            throw new RuntimeException("Cast exception, invalid token");
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

    private void validateToken(@NonNull String token, @NonNull SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parse(token);
        } catch (ExpiredJwtException expEx) {
            throw new RuntimeException("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            throw new RuntimeException("Unsupported token");
        } catch (MalformedJwtException mjEx) {
            throw new RuntimeException("Malformed token");
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }
}
