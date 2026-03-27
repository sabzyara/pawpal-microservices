package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.petmanagementservice.dto.pet.*;
import com.example.pawpalapp.petmanagementservice.mapper.PetMapper;
import com.example.pawpalapp.petmanagementservice.model.ActivityLog;
import com.example.pawpalapp.petmanagementservice.model.NutritionLog;
import com.example.pawpalapp.petmanagementservice.model.Pet;
import com.example.pawpalapp.petmanagementservice.model.PetOwner;
import com.example.pawpalapp.petmanagementservice.repository.ActivityLogRepository;
import com.example.pawpalapp.petmanagementservice.repository.NutritionLogRepository;
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
    private final ActivityLogRepository activityLogRepository;
    private final NutritionLogRepository nutritionLogRepository;

    public PetService(PetRepository petRepository, PetMapper petMapper, PetOwnerRepository petOwnerRepository, ActivityLogRepository activityLogRepository, NutritionLogRepository nutritionLogRepository) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
        this.petOwnerRepository = petOwnerRepository;
        this.activityLogRepository = activityLogRepository;
        this.nutritionLogRepository = nutritionLogRepository;
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

    public List<PetResponseDto> getByUser(Long userId) {

        PetOwner owner = petOwnerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalStateException("PetOwner not found")
                );

        Long ownerId = owner.getId();

        return petRepository.findByOwnerId(ownerId)
                .stream()
                .map(petMapper::toDto)
                .toList();
    }
    public PetFullDto getFullData(String userId) {

        Long userIdLong = Long.valueOf(userId);

        PetOwner owner = petOwnerRepository.findByUserId(userIdLong)
                .orElseThrow();

        Long ownerId = owner.getId();

        Pet pet = petRepository.findFirstByOwnerId(ownerId)
                .orElseThrow();

        List<ActivityLog> activities =
                activityLogRepository.findByPetId(pet.getId());

        List<NutritionLog> nutrition =
                nutritionLogRepository.findByPetId(pet.getId());

        PetFullDto dto = new PetFullDto();
        dto.setPet(petMapper.toDto(pet));
        dto.setActivities(activities);
        dto.setNutrition(nutrition);

        return dto;
    }

    public PetFullDto getByPetId(Long petId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow();

        List<ActivityLog> activities =
                activityLogRepository.findByPetId(petId);

        List<NutritionLog> nutrition =
                nutritionLogRepository.findByPetId(petId);

        PetFullDto dto = new PetFullDto();
        dto.setPet(petMapper.toDto(pet));
        dto.setActivities(activities);
        dto.setNutrition(nutrition);

        return dto;
    }
}
