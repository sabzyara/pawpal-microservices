package com.example.pawpalapp.specialistservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication(scanBasePackages = "com.example.pawpalapp")
@EnableDiscoveryClient
public class SpecialistServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpecialistServiceApplication.class, args);
    }
}
