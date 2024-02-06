package com.example.sumatra.service;

import com.example.sumatra.service.request.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor

public class UserRegistrationListener {

    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {"q.user-registration"})
    public void onUserRegistration(UserRegistrationRequest event) {

        Consumer<String> sendToQueue = queue -> rabbitTemplate.convertAndSend("x.post-registration", queue, event);

        Consumer<String> logMessage = msg -> log.info("User Registration {}: {}", msg, event);

        boolean result = userService.registerUser(event);

        if (result) {
            logMessage.accept("Successful");
        } else {
            sendToQueue.accept("failed-registration");
            logMessage.accept("Failed");
        }
    }
}
