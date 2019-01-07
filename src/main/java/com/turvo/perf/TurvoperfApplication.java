package com.turvo.perf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TurvoperfApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurvoperfApplication.class, args);
	}

}

