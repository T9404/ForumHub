package com.example;

import com.example.security.EnableSecurity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableSecurity
@EnableFeignClients
@SpringBootApplication
public class NotificationApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(NotificationApplication.class)
                .beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
                .run(args);
    }
}
