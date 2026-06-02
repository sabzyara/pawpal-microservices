package com.example.pawpalapp.appointmentservice.dto;


import com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class AppointmentUpdateDto {
    private String ownerNotes;
    private String specialistNotes;
    private AppointmentStatus status;
}