package com.example.core.auth.mail.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.email")
public class MailMessageProperty {
    private String subject;
    private String content;
}
