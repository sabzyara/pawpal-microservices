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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    // TOKEN
    private HttpEntity<Void> createEntityWithToken() {
        try {
            Jwt jwt = (Jwt) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            String token = jwt.getTokenValue();

            System.out.println("TOKEN: " + token);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            return new HttpEntity<>(headers);

        } catch (Exception e) {
            throw new RuntimeException("TOKEN ERROR: " + e.getMessage(), e);
        }
    }

    // USERS
    public List<UserResponseDto> getByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    // PETS
    public List<PetDto> getPetsByUserId(Long userId) {

        String url = "https://pawpal-gateway.onrender.com/pet-management/api/pets/" + userId;
        HttpEntity<Void> entity = createEntityWithToken();

        try {
            System.out.println("CALL: " + url);

            ResponseEntity<PetDto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PetDto[].class
            );

            return response.getBody() != null
                    ? Arrays.asList(response.getBody())
                    : List.of();

        } catch (HttpClientErrorException e) {
            System.out.println("PETS CLIENT ERROR: " + e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("PETS ERROR: " + e.getMessage(), e);
        }
    }

    // FULL PROFILE
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

    // OWNER
    public OwnerFullDto getOwnerProfile(Long userId) {

        HttpEntity<Void> entity = createEntityWithToken();

        String ownerUrl = "https://pawpal-gateway.onrender.com/pet-management/api/pet-owners/user/" + userId;
        String petsUrl = "https://pawpal-gateway.onrender.com/pet-management/api/pets/" + userId;

        PetOwnerDto owner = null;
        List<PetDto> petList = List.of();

        try {
            System.out.println("CALL OWNER: " + ownerUrl);

            ResponseEntity<PetOwnerDto> ownerResponse =
                    restTemplate.exchange(ownerUrl, HttpMethod.GET, entity, PetOwnerDto.class);

            owner = ownerResponse.getBody();

        } catch (HttpClientErrorException e) {
            System.out.println("OWNER ERROR: " + e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
        }

        try {
            System.out.println("CALL PETS: " + petsUrl);

            ResponseEntity<PetDto[]> petsResponse =
                    restTemplate.exchange(petsUrl, HttpMethod.GET, entity, PetDto[].class);

            PetDto[] pets = petsResponse.getBody();
            petList = pets != null ? Arrays.asList(pets) : List.of();

        } catch (HttpClientErrorException e) {
            System.out.println("PETS ERROR: " + e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
        }

        System.out.println("OWNER: " + owner);
        System.out.println("PETS: " + petList);

        return new OwnerFullDto(owner, petList);
    }

    // VET
    public VetDto getVetProfile(Long userId) {

        String url = "https://pawpal-gateway.onrender.com/specialist-service/api/veterinarians/user/" + userId;
        HttpEntity<Void> entity = createEntityWithToken();

        try {
            System.out.println("CALL VET: " + url);

            ResponseEntity<VetDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    VetDto.class
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {
            System.out.println("VET ERROR: " + e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("VET ERROR: " + e.getMessage(), e);
        }
    }

    // SERVICE
    public ServiceProviderDto getServiceProfile(Long userId) {

        String url = "https://pawpal-gateway.onrender.com/specialist-service/api/service-providers/user/" + userId;
        HttpEntity<Void> entity = createEntityWithToken();

        try {
            System.out.println("CALL SERVICE: " + url);

            ResponseEntity<ServiceProviderDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ServiceProviderDto.class
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {
            System.out.println("SERVICE ERROR: " + e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("SERVICE ERROR: " + e.getMessage(), e);
        }
    }
}