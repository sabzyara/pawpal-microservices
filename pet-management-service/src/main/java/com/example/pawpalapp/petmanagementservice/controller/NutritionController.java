package com.example.pawpalapp.petmanagementservice.controller;

import com.example.pawpalapp.petmanagementservice.dto.nutrition.*;
import com.example.pawpalapp.petmanagementservice.service.NutritionService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

    private final NutritionService service;

    public NutritionController(NutritionService service) {
        this.service = service;
    }

    @PostMapping
    public NutritionResponseDto create(@RequestBody NutritionCreateDto dto) {
        return service.create(dto);
    }

    @GetMapping("/pet/{petId}")
    public List<NutritionResponseDto> getByPet(@PathVariable Long petId) {
        return service.getByPet(petId);
    }

    @GetMapping("/pet/{petId}")
    public List<NutritionResponseDto> getByPet(
            @PathVariable Long petId,
            @RequestParam(required = false) LocalDate date
    ) {
        return service.getByPetAndDate(petId, date);
    }
}
