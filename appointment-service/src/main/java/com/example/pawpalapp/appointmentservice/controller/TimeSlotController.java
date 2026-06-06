package com.example.pawpalapp.appointmentservice.controller;

import com.example.pawpalapp.appointmentservice.dto.TimeSlotAvailableRequestDto;
import com.example.pawpalapp.appointmentservice.dto.TimeSlotResponseDto;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.service.SlotGenerationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
            @RequestParam @NotNull Long specialistId,
            @RequestParam @NotNull SpecialistType specialistType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate date,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(slotGenerationService.getAvailableSlots(specialistId, specialistType, date, pageable));
    }

    @PostMapping("/available")
    public ResponseEntity<Page<TimeSlotResponseDto>> getAvailableSlotsByDto(
            @Valid @RequestBody TimeSlotAvailableRequestDto request,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(slotGenerationService.getAvailableSlots(request, pageable));
    }

    @GetMapping("/date")
    public ResponseEntity<List<TimeSlotResponseDto>> getSlotsByDate(
            @RequestParam @NotNull Long specialistId,
            @RequestParam @NotNull SpecialistType specialistType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate date) {

        return ResponseEntity.ok(slotGenerationService.getSlotsByDate(specialistId, specialistType, date));
    }

    @GetMapping("/my-available")
    public ResponseEntity<Page<TimeSlotResponseDto>> getMyAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate date,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(slotGenerationService.getMyAvailableSlots(date, pageable));
    }

    @PostMapping("/{slotId}/block")
    public ResponseEntity<TimeSlotResponseDto> blockSlot(
            @PathVariable Long slotId,
            @RequestParam @NotNull String reason) {

        return ResponseEntity.ok(slotGenerationService.blockSlot(slotId, reason));
    }

    @PostMapping("/{slotId}/unblock")
    public ResponseEntity<TimeSlotResponseDto> unblockSlot(
            @PathVariable Long slotId) {

        return ResponseEntity.ok(slotGenerationService.unblockSlot(slotId));
    }

    @PostMapping("/regenerate")
    public ResponseEntity<Void> regenerateSlotsForDate(
            @RequestParam @NotNull Long specialistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate date) {

        slotGenerationService.regenerateSlotsForDate(specialistId, date);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/regenerate-all")
    public ResponseEntity<Void> regenerateAllSlots(@RequestParam @NotNull Long specialistId) {
        slotGenerationService.generateSlotsForAllSchedulesOfSpecialist(specialistId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanupOldSlots() {
        slotGenerationService.cleanupOldSlots();
        return ResponseEntity.ok("Cleanup completed");
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> hasSlotsOnDate(
            @RequestParam @NotNull Long specialistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate date) {

        List<TimeSlotResponseDto> slots = slotGenerationService.getSlotsByDate(specialistId, null, date);
        return ResponseEntity.ok(!slots.isEmpty());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<TimeSlotResponseDto>> getUpcomingSlots(
            @RequestParam @NotNull Long specialistId,
            @RequestParam @NotNull SpecialistType specialistType,
            @RequestParam(defaultValue = "7") int days) {

        List<TimeSlotResponseDto> allSlots = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < days; i++) {
            LocalDate date = today.plusDays(i);
            allSlots.addAll(slotGenerationService.getSlotsByDate(specialistId, specialistType, date));
        }

        return ResponseEntity.ok(allSlots);
    }
}