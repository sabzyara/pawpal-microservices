package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.petmanagementservice.dto.pet.*;
import com.example.pawpalapp.petmanagementservice.mapper.PetMapper;
import com.example.pawpalapp.petmanagementservice.model.Pet;
import com.example.pawpalapp.petmanagementservice.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    public PetService(PetRepository petRepository, PetMapper petMapper) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
    }

    public PetResponseDto create(PetCreateDto dto) {
        Pet pet = petMapper.toEntity(dto);
        return petMapper.toDto(petRepository.save(pet));
    }

    public List<PetResponseDto> getByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId)
                .stream()
                .map(petMapper::toDto)
                .collect(Collectors.toList());
    }
}
