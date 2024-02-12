package com.example.sumatra.command;

import com.example.sumatra.service.request.UserRegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ConsoleRunner implements ApplicationRunner {

    private static final String Q_USER_REGISTRATION =  "q.user-registration";
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper  objectMapper = new ObjectMapper();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            int nextInt = random.nextInt(10000);
            UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
                    .userName("John Doe " + i)
                    .initBalance(String.valueOf(nextInt))
                    .phoneNumbers(List.of(new UserRegistrationRequest.PhoneNumber(String.valueOf(79130000000L + i))))
                    .emailAddresses(List.of(new UserRegistrationRequest.EmailAddress("john.doe@example.com" +i)))
                    .dateOfBirth("1990-01-01")
                    .userPassword("securepassword123" + i)
                    .build();

            String json = objectMapper.writeValueAsString(userRegistrationRequest);

            rabbitTemplate.convertAndSend(Q_USER_REGISTRATION, json);
        }



    }
}
