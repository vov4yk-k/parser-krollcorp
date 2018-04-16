package com.vkruk.parserkrollcorp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
public class ParserKrollcorpApplication {

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kiev"));
	}

	public static void main(String[] args) {
		SpringApplication.run(ParserKrollcorpApplication.class, args);
	}

}
