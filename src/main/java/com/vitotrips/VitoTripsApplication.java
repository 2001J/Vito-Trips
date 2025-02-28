package com.vitotrips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class VitoTripsApplication {

	public static void main(String[] args) {
		// Check if we're running in Docker and set the profile accordingly
		boolean runningInDocker = System.getenv("DOCKER_CONTAINER") != null;
		if (runningInDocker) {
			System.setProperty("spring.profiles.active", "docker");
		}
		
		SpringApplication.run(VitoTripsApplication.class, args);
	}

}
