package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.petmanagementservice.dto.nutrition.*;
import com.example.pawpalapp.petmanagementservice.mapper.NutritionMapper;
import com.example.pawpalapp.petmanagementservice.model.NutritionLog;
import com.example.pawpalapp.petmanagementservice.repository.NutritionLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NutritionService {

    private final NutritionLogRepository repository;
    private final NutritionMapper mapper;

    public NutritionService(NutritionLogRepository repository, NutritionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public NutritionResponseDto create(NutritionCreateDto dto) {
        NutritionLog log = mapper.toEntity(dto);
        return mapper.toDto(repository.save(log));
    }

    public List<NutritionResponseDto> getByPet(Long petId) {
        return repository.findByPetId(petId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    public List<NutritionResponseDto> getByPetAndDate(Long petId, LocalDate date) {
        if (date != null) {
            return repository.findByPetIdAndDate(petId, date)
                    .stream()
                    .map(mapper::toDto)
                    .toList();
        }

        return repository.findByPetId(petId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
