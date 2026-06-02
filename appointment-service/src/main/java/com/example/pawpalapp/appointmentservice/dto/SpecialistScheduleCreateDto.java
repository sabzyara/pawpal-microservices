package com.example.pawpalapp.appointmentservice.dto;

import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@Data
public class SpecialistScheduleCreateDto {
    private Long specialistId;
    private SpecialistType specialistType;
    private DayOfWeek dayOfWeek;
    private LocalTime workStart;
    private LocalTime workEnd;
    private LocalTime breakStart;
    private LocalTime breakEnd;
    private Integer slotDurationMinutes;
}