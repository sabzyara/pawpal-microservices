package com.example.pawpalapp.petmanagementservice.controller;

import com.example.pawpalapp.petmanagementservice.dto.activity.*;
import com.example.pawpalapp.petmanagementservice.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }

    @PostMapping
    public ActivityResponseDto create(@RequestBody ActivityCreateDto dto) {
        return service.create(dto);
    }

    @GetMapping("/pet/{petId}")
    public List<ActivityResponseDto> getByPet(@PathVariable Long petId) {
        return service.getByPet(petId);
    }
}
