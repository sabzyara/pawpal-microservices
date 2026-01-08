package com.example.pawpalapp.appointmentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class SpecialistScheduleCreateDto {
    private DayOfWeek dayOfWeek;

    private LocalTime workStart;

    private LocalTime workEnd;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    private Integer slotDurationMinutes;
}
