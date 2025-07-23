package com.service.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient  // Register with Eureka for service discovery
public class AuthServiceApplication {

	public static void main(String[] args) {
		System.out.println("Starting Authentication Service...");
		SpringApplication.run(AuthServiceApplication.class, args);
		System.out.println("Auth Service started successfully!");
		System.out.println("Service available at: http://localhost:8081");
		System.out.println("Access via API Gateway: http://localhost:8080/api/auth/*");
	}

	// Add RestTemplate for inter-service communication
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}