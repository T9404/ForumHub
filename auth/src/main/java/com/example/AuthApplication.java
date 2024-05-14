package com.example;

import com.example.security.EnableSecurity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableSecurity
@EnableScheduling
@SpringBootApplication
public class AuthApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthApplication.class)
                .beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
                .run(args);
    }
}
