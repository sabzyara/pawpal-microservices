package com.example.pawpalapp.specialistservice.controller;

import com.example.pawpalapp.specialistservice.dto.SpecialistCreateDto;
import com.example.pawpalapp.specialistservice.dto.SpecialistResponseDto;
import com.example.pawpalapp.specialistservice.model.Specialist;
import com.example.pawpalapp.specialistservice.service.SpecialistService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/specialists")
//public class SpecialistController {
//
//    private final SpecialistService specialistService;
//
//    public SpecialistController(SpecialistService specialistService) {
//        this.specialistService = specialistService;
//    }
//
//    @PostMapping
//    public SpecialistResponseDto create(@RequestBody SpecialistCreateDto dto) {
//        return specialistService.create(dto);
//    }
//
//    @GetMapping("/by-user/{userId}")
//    public SpecialistResponseDto getByUserId(@PathVariable Long userId) {
//        return specialistService.getByUserId(userId);
//    }
//}

@RestController
@RequestMapping("/specialists")
@RequiredArgsConstructor
public class SpecialistController {

    private final SpecialistService specialistService;

    @PostMapping
    public Specialist create(@RequestBody SpecialistCreateDto dto) {
        return specialistService.create(dto.getUserId(), dto.getType());
    }

    @GetMapping
    public List<Specialist> getAll() {
        return specialistService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        specialistService.delete(id);
    }
}


