package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.petmanagementservice.dto.activity.*;
import com.example.pawpalapp.petmanagementservice.mapper.ActivityMapper;
import com.example.pawpalapp.petmanagementservice.model.ActivityLog;
import com.example.pawpalapp.petmanagementservice.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityLogRepository repository;
    private final ActivityMapper mapper;

    public ActivityService(ActivityLogRepository repository, ActivityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ActivityResponseDto create(ActivityCreateDto dto) {
        ActivityLog log = mapper.toEntity(dto);
        return mapper.toDto(repository.save(log));
    }

    public List<ActivityResponseDto> getByPet(Long petId) {
        return repository.findByPetId(petId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
