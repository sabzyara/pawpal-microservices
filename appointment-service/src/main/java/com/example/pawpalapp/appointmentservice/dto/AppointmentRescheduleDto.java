package com.example.pawpalapp.appointmentservice.dto;

import lombok.Data;

@Data
public class AppointmentRescheduleDto {

    private Long appointmentId;

    private Long newTimeSlotId;

    private String reason;
}