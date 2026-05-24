package com.example.pawpalapp.appointmentservice.dto;


import com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Data
public class AppointmentUpdateDto {
    private LocalDate date;
    private LocalTime startTime;
    private String ownerNotes;
    private String specialistNotes;
    private AppointmentStatus status;
}