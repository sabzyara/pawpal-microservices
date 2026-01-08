package com.example.pawpalapp.petmanagementservice.dto.ai;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationRequestDto {

    private String species;
    private int weight;
    private int age;

    private int totalActivityMinutes;
    private int totalCalories;
}
