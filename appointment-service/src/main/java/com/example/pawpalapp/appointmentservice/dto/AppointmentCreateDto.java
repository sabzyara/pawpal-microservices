package com.example.pawpalapp.appointmentservice.dto;

import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentCreateDto {

    private Long specialistId;

    private SpecialistType specialistType;

    private Long petId;

    private LocalDate date;

    private LocalTime startTime;
}

