package com.example.pawpalapp.appointmentservice.controller;


import com.example.pawpalapp.appointmentservice.dto.TimeSlotResponseDto;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.service.SlotGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final SlotGenerationService slotGenerationService;

    @GetMapping("/available")
    public ResponseEntity<Page<TimeSlotResponseDto>> getAvailableSlots(
            @RequestParam Long specialistId,
            @RequestParam SpecialistType specialistType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(slotGenerationService.getAvailableSlots(specialistId, specialistType, date, pageable));
    }
}