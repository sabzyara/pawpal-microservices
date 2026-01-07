package com.example.pawpalapp.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                . authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/veterinarians").hasAnyRole("VET", "ADMIN")
                        .requestMatchers("/api/service-providers").hasAnyRole("SERVICE", "ADMIN")
                        .requestMatchers("/api/pets",
                                "/api/nutrition",
                                "/api/activities",
                                "/api/activities/pet/",
                                "/api/nutrition/pet/").hasAnyRole("OWNER", "ADMIN")
                        .requestMatchers("/users").permitAll()
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
