package com.example.pawpalapp.petmanagementservice.controller;

import com.example.pawpalapp.petmanagementservice.dto.pet.*;
import com.example.pawpalapp.petmanagementservice.service.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public PetResponseDto create(@RequestBody PetCreateDto dto) {
        return petService.create(dto);
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetResponseDto> getByOwner(@PathVariable Long ownerId) {
        return petService.getByOwner(ownerId);
    }
}
