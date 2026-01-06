package com.example.pawpalapp.userservice.controller;

import com.example.pawpalapp.userservice.dto.UserRegisterDto;
import com.example.pawpalapp.userservice.dto.UserResponseDto;
import com.example.pawpalapp.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final UserService userService;
//
//    @PostMapping
//    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterDto dto) {
//        return ResponseEntity.ok(userService.register(dto));
//    }
//}
