package com.example.pawpalapp.appointmentservice.controller;

import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleCreateDto;
import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleResponseDto;
import com.example.pawpalapp.appointmentservice.dto.TimeSlotResponseDto;
import com.example.pawpalapp.appointmentservice.service.SlotGenerationService;
import com.example.pawpalapp.appointmentservice.service.SpecialistScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class SpecialistScheduleController {

    private final SpecialistScheduleService scheduleService;
    private final SlotGenerationService slotGenerationService;

    @PostMapping
    public ResponseEntity<SpecialistScheduleResponseDto> createOrUpdateSchedule(@RequestBody SpecialistScheduleCreateDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.createOrUpdateSchedule(request));
    }

    @GetMapping
    public ResponseEntity<List<SpecialistScheduleResponseDto>> getMySchedules() {
        return ResponseEntity.ok(scheduleService.getMySchedules());
    }

    @DeleteMapping("/{dayOfWeek}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteSchedule(@PathVariable DayOfWeek dayOfWeek) {
        scheduleService.deleteSchedule(dayOfWeek);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/regenerate")
    public ResponseEntity<Void> regenerateSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        scheduleService.regenerateSlotsForDate(date);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate")
    public ResponseEntity<List<TimeSlotResponseDto>> generateSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(slotGenerationService.generateSlotsForDate(date));
    }
}