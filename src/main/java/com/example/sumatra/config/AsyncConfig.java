package com.example.sumatra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



@Configuration
public class AsyncConfig {
    @Bean
    public Lock taskLock() {
        return new ReentrantLock();
    }
}
