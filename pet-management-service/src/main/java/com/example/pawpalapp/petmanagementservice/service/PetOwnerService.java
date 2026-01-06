package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.petmanagementservice.dto.petowner.*;
import com.example.pawpalapp.petmanagementservice.mapper.PetOwnerMapper;
import com.example.pawpalapp.petmanagementservice.model.PetOwner;
import com.example.pawpalapp.petmanagementservice.repository.PetOwnerRepository;
import org.springframework.stereotype.Service;

@Service
public class PetOwnerService {

    private final PetOwnerRepository repository;
    private final PetOwnerMapper mapper;

    public PetOwnerService(PetOwnerRepository repository, PetOwnerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PetOwnerResponseDto create(PetOwnerCreateDto dto) {
        PetOwner owner = mapper.toEntity(dto);
        return mapper.toDto(repository.save(owner));
    }
}
