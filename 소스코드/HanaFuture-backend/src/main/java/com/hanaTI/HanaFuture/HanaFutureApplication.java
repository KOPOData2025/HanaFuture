package com.hanaTI.HanaFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HanaFutureApplication {

	public static void main(String[] args) {
		SpringApplication.run(HanaFutureApplication.class, args);
	}

}
