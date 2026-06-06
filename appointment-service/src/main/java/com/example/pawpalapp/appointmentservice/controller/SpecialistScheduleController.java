package com.example.pawpalapp.appointmentservice.controller;

import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleCreateDto;
import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleResponseDto;
import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleUpdateDto;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.service.SpecialistScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class SpecialistScheduleController {

    private final SpecialistScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<SpecialistScheduleResponseDto> createSchedule(
            @Valid @RequestBody SpecialistScheduleCreateDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleService.createSchedule(request));
    }

    @PostMapping("/weekly")
    public ResponseEntity<List<SpecialistScheduleResponseDto>> createWeeklySchedules(
            @RequestParam Long specialistId,
            @RequestParam SpecialistType specialistType,
            @Valid @RequestBody List<SpecialistScheduleCreateDto> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleService.createWeeklySchedules(specialistId, specialistType, requests));
    }

    @GetMapping("/me")
    public ResponseEntity<List<SpecialistScheduleResponseDto>> getMySchedules() {
        return ResponseEntity.ok(scheduleService.getMySchedules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialistScheduleResponseDto> getScheduleById(
            @PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<SpecialistScheduleResponseDto> getScheduleByDay(
            @PathVariable DayOfWeek dayOfWeek) {
        return ResponseEntity.ok(scheduleService.getScheduleByDay(dayOfWeek));
    }

    @GetMapping("/specialist/{specialistId}")
    public ResponseEntity<List<SpecialistScheduleResponseDto>> getSchedulesBySpecialist(
            @PathVariable Long specialistId) {
        return ResponseEntity.ok(scheduleService.getSchedulesBySpecialist(specialistId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialistScheduleResponseDto> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody SpecialistScheduleUpdateDto request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/day/{dayOfWeek}")
    public ResponseEntity<Void> deleteScheduleByDay(
            @PathVariable DayOfWeek dayOfWeek) {
        scheduleService.deleteScheduleByDay(dayOfWeek);
        return ResponseEntity.noContent().build();
    }
}