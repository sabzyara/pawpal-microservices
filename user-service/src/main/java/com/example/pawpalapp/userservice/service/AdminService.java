package com.example.pawpalapp.userservice.service;

import com.example.pawpalapp.userservice.dto.*;
import com.example.pawpalapp.userservice.mapper.UserMapper;
import com.example.pawpalapp.userservice.model.User;
import com.example.pawpalapp.userservice.model.enums.Role;
import com.example.pawpalapp.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;



    // GET USERS BY ROLE
    public List<UserResponseDto> getByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public List<PetDto> getPetsByUserId(Long userId) {

        String url = "https://pawpal-gateway.onrender.com/pet-management-service/api/pets/" + userId;

//        String url = "http://localhost:8081/api/pets/" + userId;

        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String token = jwt.getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PetDto[]> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        PetDto[].class
                );

        return Arrays.asList(response.getBody());
    }

    public Object getFullProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return switch (user.getRole()) {

            case OWNER -> getOwnerProfile(userId);
            case VET -> getVetProfile(userId);
            case SERVICE -> getServiceProfile(userId);
            default -> throw new RuntimeException("Unsupported role");
        };
    }

    public OwnerFullDto getOwnerProfile(Long userId) {

        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String token = jwt.getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // профиль
        PetOwnerDto owner = restTemplate.exchange(
//                "http://localhost:8081/api/pet-owners/user/" + userId,
                "https://pawpal-gateway.onrender.com/pet-management-service/api/pet-owners/user" + userId,

                HttpMethod.GET,
                entity,
                PetOwnerDto.class
        ).getBody();

        // питомцы
        PetDto[] pets = restTemplate.exchange(
//                "http://localhost:8081/api/pets/" + userId,
                "https://pawpal-gateway.onrender.com/pet-management-service/api/pets/" + userId,
                HttpMethod.GET,
                entity,
                PetDto[].class
        ).getBody();

        System.out.println("OWNER: " + owner);
        System.out.println("PETS: " + Arrays.toString(pets));

        List<PetDto> petList = pets != null ? Arrays.asList(pets) : List.of();
        return new OwnerFullDto(owner, petList);
    }

    public VetDto getVetProfile(Long userId) {

        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String token = jwt.getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
//                "http://localhost:8083/api/veterinarians/user/" + userId,
                   "https://pawpal-gateway.onrender.com/specialist-service/api/veterinarians/user" + userId,

                HttpMethod.GET,
                entity,
                VetDto.class
        ).getBody();
    }

    public ServiceProviderDto getServiceProfile(Long userId) {

        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String token = jwt.getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
//                "http://localhost:8083/api/service-providers/user/" + userId,
                   "https://pawpal-gateway.onrender.com/specialist-service/api/service-providers/user" + userId,

                HttpMethod.GET,
                entity,
                ServiceProviderDto.class
        ).getBody();
    }
}
