package com.example.pawpalapp.petmanagementservice.mapper;

import com.example.pawpalapp.petmanagementservice.dto.pet.*;
import com.example.pawpalapp.petmanagementservice.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public Pet toEntity(PetCreateDto dto) {
        return Pet.builder()
                .ownerId(dto.getOwnerId())
                .name(dto.getName())
                .species(dto.getSpecies())
                .breed(dto.getBreed())
                .gender(dto.getGender())
                .age(dto.getAge())
                .weight(dto.getWeight())
                .healthStatus(dto.getHealthStatus())
                .build();
    }

    public PetResponseDto toDto(Pet pet) {
        return PetResponseDto.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .age(pet.getAge())
                .weight(pet.getWeight())
                .healthStatus(pet.getHealthStatus())
                .build();
    }
}
