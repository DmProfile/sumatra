package com.example.sumatra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.addresses}")
    private String rabbitmqAddresses;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    @Bean
    public Queue createUserRegistrationQueue() {
        return new Queue("q.user-registration");
    }

    @Bean
    public Declarables createPostRegistrationSchema() {
        FanoutExchange postRegistrationExchange = new FanoutExchange("x.post-registration");

        Queue failedRegistrationQueue = new Queue("q.failed-registration");

        Declarables declarables = new Declarables(
                postRegistrationExchange,
                failedRegistrationQueue,
                new Binding(failedRegistrationQueue.getName(), Binding.DestinationType.QUEUE, postRegistrationExchange.getName(), "failed-registration", null)
        );

        return declarables;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitmqAddresses, rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        return connectionFactory;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
