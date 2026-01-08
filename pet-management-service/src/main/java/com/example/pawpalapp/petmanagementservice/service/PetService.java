package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.petmanagementservice.dto.pet.*;
import com.example.pawpalapp.petmanagementservice.mapper.PetMapper;
import com.example.pawpalapp.petmanagementservice.model.Pet;
import com.example.pawpalapp.petmanagementservice.model.PetOwner;
import com.example.pawpalapp.petmanagementservice.repository.PetOwnerRepository;
import com.example.pawpalapp.petmanagementservice.repository.PetRepository;
import com.example.pawpalapp.security.AuthUser;
import com.example.pawpalapp.security.Role;
import com.example.pawpalapp.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final PetOwnerRepository petOwnerRepository;

    public PetService(PetRepository petRepository, PetMapper petMapper, PetOwnerRepository petOwnerRepository) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
        this.petOwnerRepository = petOwnerRepository;
    }

    public PetResponseDto create(PetCreateDto dto) {

        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.OWNER) {
            throw new AccessDeniedException("Only pet owners can create pet");
        }

        Long targetUserId = current.userId();

        PetOwner owner = petOwnerRepository.findByUserId(targetUserId)
                .orElseThrow(() ->
                        new IllegalStateException("PetOwner profile not found for userId=" + targetUserId)
                );

        Long ownerId = owner.getId();

        Pet pet = new Pet();
        pet.setOwnerId(ownerId);
        pet.setName(dto.getName());
        pet.setAge(dto.getAge());
        pet.setBreed(dto.getBreed());
        pet.setGender(dto.getGender());
        pet.setSpecies(dto.getSpecies());
        pet.setWeight(dto.getWeight());
        pet.setHealthStatus(dto.getHealthStatus());

        Pet saved = petRepository.save(pet);
        return petMapper.toDto(saved);
    }

    public List<PetResponseDto> getByOwner() {

        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.OWNER) {
            throw new AccessDeniedException("Only pet owners can see pet list");
        }

        Long targetUserId = current.userId();

        PetOwner owner = petOwnerRepository.findByUserId(targetUserId)
                .orElseThrow(() ->
                        new IllegalStateException("PetOwner profile not found for userId=" + targetUserId)
                );

        Long ownerId = owner.getId();

        return petRepository.findByOwnerId(ownerId)
                .stream()
                .map(petMapper::toDto)
                .collect(Collectors.toList());
    }
}
