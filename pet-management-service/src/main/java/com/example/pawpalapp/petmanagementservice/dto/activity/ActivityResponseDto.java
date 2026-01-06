package com.example.pawpalapp.petmanagementservice.dto.activity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponseDto {
    private Long activityId;
    private String summary;
}
