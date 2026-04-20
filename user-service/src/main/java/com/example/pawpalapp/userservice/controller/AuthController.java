package com.example.pawpalapp.userservice.controller;

import com.example.pawpalapp.userservice.dto.LoginRequest;
import com.example.pawpalapp.userservice.dto.UserRegisterDto;
import com.example.pawpalapp.userservice.dto.UserResponseDto;
import com.example.pawpalapp.userservice.service.AuthService;
import com.thoughtworks.xstream.core.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public void register(@RequestBody UserRegisterDto    request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(
                request.getEmail(),
                request.getPassword()
        );
    }

    @GetMapping("/me")
    public UserResponseDto me(HttpServletRequest request) {
        return authService.getCurrentUser(request);
    }
}

