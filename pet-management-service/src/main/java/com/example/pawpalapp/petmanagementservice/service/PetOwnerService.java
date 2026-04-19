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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetOwnerService {

    private final PetOwnerRepository petOwnerRepository;

    public PetOwnerService(PetOwnerRepository repository) {
        this.petOwnerRepository = repository;
    }

    // CREATE
    public void createMyProfile(PetOwnerCreateDto request) {

        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.OWNER) {
            throw new AccessDeniedException("Only OWNER can create profile");
        }

        Long userId = current.userId();

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

    // UPDATE
    public PetOwnerResponseDto update(PetOwnerUpdateDto dto) {

        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.OWNER) {
            throw new AccessDeniedException("Only pet owners can update profile");
        }

        Long userId = current.userId();

        PetOwner petOwner = petOwnerRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Pet owner profile not found"));

        PetOwnerMapper.updateEntity(petOwner, dto);

        PetOwner saved = petOwnerRepository.save(petOwner);

        return PetOwnerMapper.toDto(saved);
    }

    // GET
    public PetOwnerResponseDto getMyProfile() {

        AuthUser current = SecurityUtils.current();

        Long userId = current.userId();

        PetOwner owner = petOwnerRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return PetOwnerMapper.toDto(owner);
    }
}