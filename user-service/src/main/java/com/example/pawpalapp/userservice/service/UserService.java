package com.example.pawpalapp.userservice.service;

import com.example.pawpalapp.userservice.dto.UserCreateDto;
import com.example.pawpalapp.userservice.dto.UserResponseDto;
import com.example.pawpalapp.userservice.mapper.UserMapper;
import com.example.pawpalapp.userservice.model.User;
import com.example.pawpalapp.userservice.model.enums.Role;
import com.example.pawpalapp.userservice.model.enums.UserStatus;
import com.example.pawpalapp.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // CREATE USER
    public UserResponseDto createUser(UserCreateDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("This user already exists");
        }
        User user = UserMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());

        if (userDto.getRole() == Role.OWNER || userDto.getRole() == Role.ADMIN) {
            user.setStatus(UserStatus.ACTIVE);
        } else {
            user.setStatus(UserStatus.PENDING);
        }

        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    // GET ALL USERS
    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    // DELETE USER
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User is not found");
        }
        userRepository.deleteById(id);
    }

    // GET USER BY ID
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toDto(user);
    }

    // STATUS UPDATE
    public void updateStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setStatus(status);
    }


}
