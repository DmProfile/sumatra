package com.example.sumatra.container;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

@Testcontainers
public interface RabbitTestContainer {

    String RABBIT_IMAGE = "rabbitmq:3";

    Map<String, String> PROPERTIES = new HashMap<>();


    RabbitMQContainer RABBIT_MQ_CONTAINER = new RabbitMQContainer(RABBIT_IMAGE);

    static void init() {

        PROPERTIES.put("spring.rabbitmq.addresses", RABBIT_MQ_CONTAINER.getHost());
        PROPERTIES.put("spring.rabbitmq.port", RABBIT_MQ_CONTAINER.getMappedPort(5672).toString());
        PROPERTIES.put("spring.rabbitmq.username", RABBIT_MQ_CONTAINER.getAdminUsername());
        PROPERTIES.put("spring.rabbitmq.password", RABBIT_MQ_CONTAINER.getAdminPassword());
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        init();
        PROPERTIES.forEach((key, value) -> registry.add(key, () -> value));
    }
}
