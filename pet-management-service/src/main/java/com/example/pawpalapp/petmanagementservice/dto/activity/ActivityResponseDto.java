package com.example.pawpalapp.petmanagementservice.dto.activity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponseDto {
    private Long activityId;
    private String summary;
    private LocalDate date;
}
