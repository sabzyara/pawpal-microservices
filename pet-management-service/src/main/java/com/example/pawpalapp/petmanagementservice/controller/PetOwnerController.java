package com.example.pawpalapp.petmanagementservice.controller;

import com.example.pawpalapp.petmanagementservice.dto.petowner.*;
import com.example.pawpalapp.petmanagementservice.service.PetOwnerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet-owners")
public class PetOwnerController {

    private final PetOwnerService service;

    public PetOwnerController(PetOwnerService service) {
        this.service = service;
    }

    @PostMapping
    public PetOwnerResponseDto create(@RequestBody PetOwnerCreateDto dto) {
        return service.create(dto);
    }
}
