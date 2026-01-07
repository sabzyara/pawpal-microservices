package com.example.pawpalapp.security;

public record AuthUser(
        Long userId,
        Role role
) {}

