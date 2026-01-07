package com.example.pawpalapp.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


public class SecurityUtils {

    public static AuthUser current() {
        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return new AuthUser(
                jwt.getClaim("userId"),
                Role.valueOf(jwt.getClaim("role"))
        );
    }
}
