package com.example.core.jwt.property;

import com.example.core.jwt.dto.Token;
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
