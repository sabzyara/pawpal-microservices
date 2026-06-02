package com.example.pawpalapp.appointmentservice.dto;

import com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class AppointmentResponseDto {
    private Long id;
    private Long specialistId;
    private SpecialistType specialistType;
    private Long petOwnerId;
    private Long petId;
    private Long timeSlotId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String cancellationReason;
    private String ownerNotes;
    private String specialistNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}