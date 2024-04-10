package com.example.security.jwt.property;

import com.example.security.dto.token.Token;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtTokenProperty {
    private Token access;
    private Token refresh;
}
