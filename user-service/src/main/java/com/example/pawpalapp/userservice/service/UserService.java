package com.example.pawpalapp.userservice.service;

import com.example.pawpalapp.userservice.dto.UserCreateDto;
import com.example.pawpalapp.userservice.dto.UserRegisterDto;
import com.example.pawpalapp.userservice.dto.UserResponseDto;
import com.example.pawpalapp.userservice.dto.UserUpdateDto;
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
//    private final SpecialistClient specialistClient;
//
//    public UserResponseDto register(UserRegisterDto dto) {
//        if (userRepository.existsByEmail(dto.getEmail())) {
//            throw new RuntimeException("Email is already taken");
//        }
//
//        User user = new User();
//        user.setEmail(dto.getEmail());
//        user.setPassword(passwordEncoder.encode(dto.getPassword()));
//        user.setRole(dto.getRole());
//
//        if (dto.getRole() == Role.OWNER) {
//            user.setStatus(UserStatus.ACTIVE);
//        }
//        else {
//            user.setStatus(UserStatus.PENDING);
//        }
//
//        User saved = userRepository.save(user);
//
//        if (dto.getRole() == Role.VET || dto.getRole() == Role.SERVICE) {
//            specialistClient.createProfile(saved.getId(), dto.getRole());
//        }
//
//        return new UserResponseDto(
//                saved.getId(),
//                saved.getUsername(),
//                saved.getEmail(),
//                saved.getRole(),
//                saved.getStatus(),
//                saved.getCreatedAt()
//        );
//    }
//
//    public void activateUser(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow();
//        user.setStatus(UserStatus.ACTIVE);
//        userRepository.save(user);
//    }

    // CREATE USER
    public UserResponseDto createUser(UserCreateDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("This user already exists");
        }
        User user = UserMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
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

    // UPDATE USER
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        UserMapper.updateEntity(user, userDto);

        return UserMapper.toDto(user);
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

}
