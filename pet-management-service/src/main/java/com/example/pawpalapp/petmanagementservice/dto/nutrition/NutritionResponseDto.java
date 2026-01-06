package com.example.pawpalapp.petmanagementservice.dto.nutrition;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionResponseDto {
    private Long logId;
    private String summary;
}
