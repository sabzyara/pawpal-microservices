package com.example.pawpalapp.petmanagementservice.controller;

import com.example.pawpalapp.petmanagementservice.dto.petowner.*;
import com.example.pawpalapp.petmanagementservice.service.PetOwnerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pet-owners")
public class PetOwnerController {

    private final PetOwnerService petOwnerService;

    public PetOwnerController(PetOwnerService service) {
        this.petOwnerService = service;
    }

    @GetMapping("/me")
    public PetOwnerResponseDto getMyProfile() {
        return petOwnerService.getMyProfile();
    }

    @PostMapping("/me")
    public void create(@RequestBody PetOwnerCreateDto dto) {
        petOwnerService.createMyProfile(dto);
    }

    @PutMapping("/me")
    public PetOwnerResponseDto update(@RequestBody PetOwnerUpdateDto dto) {
        return petOwnerService.update(dto);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteByUserId(@PathVariable Long userId) {
        petOwnerService.deleteByUserId(userId);
    }
    @PostMapping("/me/avatar")
    public String uploadAvatar(@RequestParam MultipartFile file) {
        return petOwnerService.uploadAvatar(file);
    }
}
