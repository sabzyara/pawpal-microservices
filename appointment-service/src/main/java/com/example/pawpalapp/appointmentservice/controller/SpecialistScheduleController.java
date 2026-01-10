package com.example.pawpalapp.appointmentservice.controller;

import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleCreateDto;
import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.service.SpecialistScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class SpecialistScheduleController {

    private final SpecialistScheduleService service;

    @PostMapping
    public SpecialistSchedule createOrUpdate(@RequestBody SpecialistScheduleCreateDto request) {
        return service.createOrUpdate(request);
    }

    @GetMapping
    public List<SpecialistSchedule> getAll() {
        return service.getAll();
    }
}

