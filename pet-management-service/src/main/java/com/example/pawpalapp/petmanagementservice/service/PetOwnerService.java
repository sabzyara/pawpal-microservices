package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.petmanagementservice.dto.petowner.*;
import com.example.pawpalapp.petmanagementservice.mapper.PetOwnerMapper;
import com.example.pawpalapp.petmanagementservice.model.Pet;
import com.example.pawpalapp.petmanagementservice.model.PetOwner;
import com.example.pawpalapp.petmanagementservice.repository.PetOwnerRepository;
import com.example.pawpalapp.security.AuthUser;
import com.example.pawpalapp.security.Role;
import com.example.pawpalapp.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetOwnerService {

    private final PetOwnerRepository petOwnerRepository;

    public PetOwnerService(PetOwnerRepository repository, PetOwnerMapper mapper) {
        this.petOwnerRepository = repository;
    }


    public void createMyProfile(PetOwnerCreateDto request) {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = jwt.getClaim("userId");
        String role = jwt.getClaim("role");

        if (!"OWNER".equals(role)) {
            throw new AccessDeniedException("Only OWNER can create profile");
        }

        if (petOwnerRepository.existsByUserId(userId)) {
            throw new RuntimeException("Profile already exists");
        }

        PetOwner petOwner = new PetOwner();
        petOwner.setUserId(userId);
        petOwner.setUsername(request.getUsername());
        petOwner.setAddress(request.getAddress());
        petOwner.setPhoneNumber(request.getPhoneNumber());

        petOwnerRepository.save(petOwner);
    }

    public PetOwnerResponseDto update(PetOwnerUpdateDto dto) {

        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.OWNER) {
            throw new AccessDeniedException("Only pet owners can update vet profile");
        }

        Long targetUserId = current.userId();

        PetOwner petOwner = petOwnerRepository
                .findByUserId(targetUserId)
                .orElseThrow(() -> new RuntimeException("Pet owner profile not found"));

        PetOwnerMapper.updateEntity(petOwner, dto);

        PetOwner saved = petOwnerRepository.save(petOwner);

        return PetOwnerMapper.toDto(saved);
    }

}
