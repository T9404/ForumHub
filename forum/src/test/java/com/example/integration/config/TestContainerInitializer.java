package com.example.integration.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("unit-test")
            .withUsername("test")
            .withPassword("test");

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        postgreSQLContainer.start();
        TestPropertyValues.of(
                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                "spring.flyway.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.flyway.user=" + postgreSQLContainer.getUsername(),
                "spring.flyway.password=" + postgreSQLContainer.getPassword()
        ).applyTo(ctx.getEnvironment());
    }
}

