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

    public static Long getUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return jwt.getClaim("userId");
    }

    public static String getRole() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return jwt.getClaim("role");
    }
}
