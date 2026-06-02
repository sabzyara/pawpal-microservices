package com.example.pawpalapp.appointmentservice.controller;

import com.example.pawpalapp.appointmentservice.dto.AppointmentCreateDto;
import com.example.pawpalapp.appointmentservice.dto.AppointmentResponseDto;
import com.example.pawpalapp.appointmentservice.dto.AppointmentUpdateDto;
import com.example.pawpalapp.appointmentservice.service.AppointmentService;
import com.example.pawpalapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.pawpalapp.appointmentservice.dto.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @Valid @RequestBody AppointmentCreateDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.createAppointment(request));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<AppointmentResponseDto>> getMyAppointments(
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(appointmentService.getMyAppointments(pageable, status));
    }

    @GetMapping("/specialist/me")
    public ResponseEntity<Page<AppointmentResponseDto>> getMySpecialistAppointments(
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(appointmentService.getMySpecialistAppointments(pageable, status));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<AppointmentResponseDto>> getUpcomingAppointments(
            @RequestParam String userType,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getUpcomingAppointments(userType, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateDto request) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {
        AppointmentCancelDto cancelDto = new AppointmentCancelDto();
        cancelDto.setUserId(SecurityUtils.getUserId());
        cancelDto.setCancellationReason(reason);
        appointmentService.cancelAppointment(id, cancelDto);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointmentWithBody(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentCancelDto cancelDto) {
        appointmentService.cancelAppointment(id, cancelDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponseDto> confirmAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeAppointment(@PathVariable Long id) {
        appointmentService.completeAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponseDto> markAsNoShow(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.markAsNoShow(id));
    }

    @PostMapping("/{id}/recommendations")
    public ResponseEntity<AppointmentResponseDto> addRecommendations(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRecommendationsDto recommendationsDto) {
        return ResponseEntity.ok(appointmentService.addRecommendations(id, recommendationsDto));
    }

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<String> getRecommendations(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getRecommendationsForOwner(id));
    }


    @PostMapping("/reschedule")
    public ResponseEntity<AppointmentResponseDto> rescheduleAppointment(@Valid @RequestBody AppointmentRescheduleDto rescheduleDto) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(rescheduleDto));
    }

}