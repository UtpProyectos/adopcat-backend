package com.adocat.adocat_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdocatApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdocatApiApplication.class, args);
	}

}
