package com.example.pawpalapp.appointmentservice.dto;

import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
public class SpecialistScheduleResponseDto {
    private Long id;
    private Long specialistId;
    private SpecialistType specialistType;
    private DayOfWeek dayOfWeek;
    private LocalTime workStart;
    private LocalTime workEnd;
    private LocalTime breakStart;
    private LocalTime breakEnd;
    private Integer slotDurationMinutes;
}