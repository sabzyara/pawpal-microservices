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
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    // CREATE USER
    public UserResponseDto createUser(UserCreateDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("This user already exists");
        }
        User user = UserMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());

        user.setStatus(UserStatus.ACTIVE);

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

    public void deleteById(Long userId) {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String token = jwt.getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    "https://pawpal-gateway.onrender.com/pet-management/api/pet-owners/user/" + userId,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
            System.out.println("✅ PET OK");
        } catch (Exception e) {
            System.out.println("❌ PET ERROR: " + e.getMessage());
        }

        try {
            restTemplate.exchange(
                    "https://pawpal-gateway.onrender.com/specialist-service/api/veterinarians/user/" + userId,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
            System.out.println("✅ VET OK");
        } catch (Exception e) {
            System.out.println("❌ VET ERROR: " + e.getMessage());
        }

        try {
            restTemplate.exchange(
                    "https://pawpal-gateway.onrender.com/specialist-service/api/service-providers/user/" + userId,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
            System.out.println("✅ SERVICE OK");
        } catch (Exception e) {
            System.out.println("❌ SERVICE ERROR: " + e.getMessage());
        }

        userRepository.deleteById(userId);
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
