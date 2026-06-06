package com.example.pawpalapp.appointmentservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReminderRequestDto {

    private Long userId;
    private String type;
    private String title;
    private String message;
    private LocalDateTime scheduledAt;
}