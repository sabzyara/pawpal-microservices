package com.example.pawpalapp.petmanagementservice.dto.nutrition;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionResponseDto {
    private Long logId;
    private String summary;
    private LocalDate date;
}
