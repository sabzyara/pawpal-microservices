package com.example.pawpalapp.appointmentservice.controller;

import com.example.pawpalapp.appointmentservice.dto.TimeSlotAvailableRequestDto;
import com.example.pawpalapp.appointmentservice.dto.TimeSlotResponseDto;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.service.SlotGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @PostMapping("/available/dto")
    public ResponseEntity<Page<TimeSlotResponseDto>> getAvailableSlotsByDto(
            @Valid @RequestBody TimeSlotAvailableRequestDto request,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(slotGenerationService.getAvailableSlots(request, pageable));
    }

    @GetMapping("/date")
    public ResponseEntity<List<TimeSlotResponseDto>> getSlotsByDate(
            @RequestParam Long specialistId,
            @RequestParam SpecialistType specialistType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(slotGenerationService.getSlotsByDate(specialistId, specialistType, date));
    }

    @GetMapping("/my-available")
    public ResponseEntity<Page<TimeSlotResponseDto>> getMyAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(slotGenerationService.getMyAvailableSlots(date, pageable));
    }

    @PostMapping("/{slotId}/block")
    public ResponseEntity<TimeSlotResponseDto> blockSlot(
            @PathVariable Long slotId,
            @RequestParam String reason) {
        return ResponseEntity.ok(slotGenerationService.blockSlot(slotId, reason));
    }

    @PostMapping("/{slotId}/unblock")
    public ResponseEntity<TimeSlotResponseDto> unblockSlot(@PathVariable Long slotId) {
        return ResponseEntity.ok(slotGenerationService.unblockSlot(slotId));
    }

    @PostMapping("/regenerate")
    public ResponseEntity<Void> regenerateSlots(
            @RequestParam Long specialistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        slotGenerationService.regenerateSlotsForDate(specialistId, date);
        return ResponseEntity.ok().build();
    }
}