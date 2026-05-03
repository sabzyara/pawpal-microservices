package com.example.pawpalapp.petmanagementservice.controller;

import com.example.pawpalapp.petmanagementservice.dto.petowner.*;
import com.example.pawpalapp.petmanagementservice.model.Pet;
import com.example.pawpalapp.petmanagementservice.service.PetOwnerService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/profile/{id}")
    public ResponseEntity<PetOwnerResponseDto> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(petOwnerService.getById(id));
    }
    @GetMapping("/user/{userId}")
    public PetOwnerResponseDto getByUserId(@PathVariable Long userId) {
        return petOwnerService.getByUserId(userId);
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
    public String uploadAvatar(@RequestParam("file") MultipartFile file) {
        return petOwnerService.uploadAvatar(file);
    }
}
