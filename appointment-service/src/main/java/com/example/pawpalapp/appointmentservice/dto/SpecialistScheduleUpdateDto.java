package com.example.pawpalapp.appointmentservice.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class SpecialistScheduleUpdateDto {

    private LocalTime workStart;

    private LocalTime workEnd;

    private LocalTime breakStart;
    private LocalTime breakEnd;

    private Integer slotDurationMinutes;
}