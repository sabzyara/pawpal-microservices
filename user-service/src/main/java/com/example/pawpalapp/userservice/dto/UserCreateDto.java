package com.example.pawpalapp.userservice.dto;

import com.example.pawpalapp.userservice.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    private String email;
    private String password;
    private Role role;
}

