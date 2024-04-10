package com.example;

import com.example.security.EnableSecurity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@EnableSecurity
@EnableFeignClients
@SpringBootApplication
public class ForumApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ForumApplication.class)
                .beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
                .run(args);
    }
}
