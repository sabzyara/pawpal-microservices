package com.example.pawpalapp.petmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.example.pawpalapp")
public class PetManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetManagementServiceApplication.class, args);
	}

}
