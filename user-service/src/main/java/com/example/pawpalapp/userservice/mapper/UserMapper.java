package com.example.pawpalapp.userservice.mapper;

import com.example.pawpalapp.userservice.dto.*;
import com.example.pawpalapp.userservice.model.User;

public class UserMapper {

    public static User toEntity(UserCreateDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }

    public static UserResponseDto toDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}

