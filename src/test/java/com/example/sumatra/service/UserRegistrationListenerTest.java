package com.example.sumatra.service;

import com.example.sumatra.container.PostgresTestContainer;
import com.example.sumatra.container.RabbitTestContainer;
import com.example.sumatra.service.request.UserRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@Testcontainers
class UserRegistrationListenerTest implements PostgresTestContainer, RabbitTestContainer {

    public static final String Q_USER_REGISTRATION = "q.user-registration";
    @Autowired
    private UserService userService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private UserRegistrationListener userRegistrationListener;

    @BeforeEach
    void check() {
        assertThat(RABBIT_MQ_CONTAINER.isRunning()).isTrue();
        assertThat( POSTGRES_CONTAINER.isRunning()).isTrue();
    }

    @Test
    void testOnUserRegistration(CapturedOutput output) throws JsonProcessingException {

        UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
                .userName("John Doe")
                .initBalance("1000.00")
                .phoneNumbers(List.of(new UserRegistrationRequest.PhoneNumber("79139129225")))
                .emailAddresses(List.of(new UserRegistrationRequest.EmailAddress("john.doe@example.com")))
                .dateOfBirth("1990-01-01")
                .userPassword("securepassword123")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(userRegistrationRequest);

        rabbitTemplate.convertAndSend(Q_USER_REGISTRATION, json);

        await().atMost( Duration.ofSeconds(15))
                .until(userCreated(output), is(true));

        await().atMost(10, SECONDS).until(() -> isTaskCountCorrect(output, 2), is(true));

        assertThat(output.getErr()).isEmpty();

        assertThat(output.getOut()).contains(
                        "UserRegistrationRequest"

                )
                .contains(
                        "User Registration Successful"

                )
                .contains(
                        "Scheduled task "
                );
    }

    private static boolean isTaskCountCorrect(CapturedOutput output, int taskCount) {
        int count = 0;
        for (String line : output.getOut().split("\n")) {
            if (line.contains("Scheduled task stopped")) {
                count++;
            }
        }
        return count >= taskCount;
    }

    private Callable<Boolean> userCreated(CapturedOutput output) {
        return () -> output.getOut().contains("UserRegistrationRequest");
    }

    @BeforeAll
    static  void startContainers() {
        Startables.deepStart(Stream.of(RABBIT_MQ_CONTAINER, POSTGRES_CONTAINER)).join();
    }
}
