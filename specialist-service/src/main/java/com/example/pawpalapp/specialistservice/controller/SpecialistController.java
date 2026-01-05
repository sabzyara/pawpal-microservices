package com.example.pawpalapp.specialistservice.controller;

import com.example.pawpalapp.specialistservice.dto.SpecialistCreateDto;
import com.example.pawpalapp.specialistservice.dto.SpecialistResponseDto;
import com.example.pawpalapp.specialistservice.service.SpecialistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/specialists")
@RequiredArgsConstructor
public class SpecialistController {

    private final SpecialistService specialistService;

    @PostMapping
    public SpecialistResponseDto create(@RequestBody SpecialistCreateDto dto) {
        return specialistService.create(dto);
    }

    @GetMapping("/by-user/{userId}")
    public SpecialistResponseDto getByUserId(@PathVariable Long userId) {
        return specialistService.getByUserId(userId);
    }
}

