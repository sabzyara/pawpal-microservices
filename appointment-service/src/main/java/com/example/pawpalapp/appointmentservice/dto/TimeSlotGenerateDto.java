package com.example.pawpalapp.appointmentservice.dto;

import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TimeSlotGenerateDto {

    private Long specialistId;

    private SpecialistType specialistType;

    private LocalDate startDate;

    private LocalDate endDate;
}