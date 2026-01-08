package com.example.pawpalapp.petmanagementservice.controller;

import com.example.pawpalapp.petmanagementservice.dto.petowner.*;
import com.example.pawpalapp.petmanagementservice.service.PetOwnerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet-owners")
public class PetOwnerController {

    private final PetOwnerService petOwnerService;

    public PetOwnerController(PetOwnerService service) {
        this.petOwnerService = service;
    }

    @PostMapping("/me")
    public void create(@RequestBody PetOwnerCreateDto dto) {
        petOwnerService.createMyProfile(dto);
    }

    @PutMapping("/me")
    public PetOwnerResponseDto update(@RequestBody PetOwnerUpdateDto dto) {
        return petOwnerService.update(dto);
    }
}
