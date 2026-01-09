package com.example.pawpalapp.notificationservice.dto;

import com.example.pawpalapp.notificationservice.model.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReminderRequestDto {

    private Long userId;
    private NotificationType type;

    private String title;
    private String message;

    private LocalDateTime scheduledAt;
}
