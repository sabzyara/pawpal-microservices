package com.example.pawpalapp.appointmentservice.controller;

import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.service.SlotGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final SlotGenerationService slotGenerationService;

    @PostMapping("/generate")
    public List<TimeSlot> generate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return slotGenerationService.generateSlotsForDate(date);
    }
}

