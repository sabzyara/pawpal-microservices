package com.example.pawpalapp.appointmentservice.dto;

import lombok.Data;

@Data
public class AppointmentCancelDto {

    private Long userId;

    private String cancellationReason;
}