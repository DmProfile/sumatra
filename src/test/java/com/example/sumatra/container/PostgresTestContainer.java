package com.example.sumatra.container;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import net.datafaker.Faker;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Testcontainers
public interface PostgresTestContainer {

    int CONTAINER_PORT = 5432;
    int LOCAL_PORT = 54320;
    Map<String, String> PROPERTIES = new HashMap<>();

    String POSTGRES_IMAGE = "postgres:alpine";


    PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
            .withReuse(true)
            .withUrlParam("reWriteBatchedInserts", String.valueOf(true))
            .withExposedPorts(CONTAINER_PORT)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(LOCAL_PORT), new ExposedPort(CONTAINER_PORT)))));
    Faker faker = new Faker(new Locale("ru"));

    static void init() {
        PROPERTIES.put("spring.test.database.replace", "none");
        PROPERTIES.put("spring.datasource.url", POSTGRES_CONTAINER.getJdbcUrl() + "?currentSchema=public");
        PROPERTIES.put("spring.datasource.username", POSTGRES_CONTAINER.getUsername());
        PROPERTIES.put("spring.datasource.password", POSTGRES_CONTAINER.getPassword());
        PROPERTIES.put("spring.jpa.show-sql", "true");
        PROPERTIES.put("spring.jpa.properties.hibernate.format_sql", "true");
        PROPERTIES.put("spring.jpa.databasePlatform", "org.hibernate.dialect.PostgreSQLDialect");
        PROPERTIES.put("spring.jpa.hibernate.ddl-auto", "none");
        PROPERTIES.put("spring.liquibase.enabled", "true");
        PROPERTIES.put("hibernate.globally_quoted_identifiers", "true");
        PROPERTIES.put("spring.jpa.properties.hibernate.generate_statistics", "true");
        PROPERTIES.put("spring.jpa.properties.hibernate.jdbc.batch_size", "10");
        PROPERTIES.put("spring.jpa.properties.hibernate.order_inserts", "true");
        PROPERTIES.put("spring.jpa.properties.hibernate.default_schema", "public");
        PROPERTIES.put("spring.jpa.properties.hibernate.hbm2ddl.create_namespaces", "false");
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        init();
        PROPERTIES.forEach((key, value) -> registry.add(key, () -> value));
    }
}
