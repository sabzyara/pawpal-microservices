package com.example.pawpalapp.petmanagementservice.dto.activity;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityCreateDto {
    private Long petId;
    private LocalDate date;
    private String activityType;
    private int distance;
    private int durationInMinutes;
}