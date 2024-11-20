package com.blankcil.api.blankcilapi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class BlankcilApplication {
	public static void main(String[] args) {
		SpringApplication.run(BlankcilApplication.class, args);
	}
}
