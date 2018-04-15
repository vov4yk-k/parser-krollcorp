package com.vkruk.parserkrollcorp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ParserKrollcorpApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParserKrollcorpApplication.class, args);
	}
}
