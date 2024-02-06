package com.example.sumatra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SumatraApplication {

	public static void main(String[] args) {
		SpringApplication.run(SumatraApplication.class, args);
	}

}
