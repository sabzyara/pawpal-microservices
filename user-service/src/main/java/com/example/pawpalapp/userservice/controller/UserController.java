package com.example.pawpalapp.userservice.controller;

import com.example.pawpalapp.userservice.dto.UserCreateDto;
import com.example.pawpalapp.userservice.dto.UserResponseDto;
import com.example.pawpalapp.userservice.model.enums.UserStatus;
import com.example.pawpalapp.userservice.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Create
    @PostMapping
    public ResponseEntity<UserResponseDto> create(
            @RequestBody UserCreateDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    // Read
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    // Delete
    @DeleteMapping("/me")
    public void deleteCurrentUser() {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = jwt.getClaim("userId");

        userService.deleteById(userId);

    }
}
