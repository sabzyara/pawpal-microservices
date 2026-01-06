package com.example.pawpalapp.petmanagementservice.dto.nutrition;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionCreateDto {
    private Long petId;
    private LocalDate date;
    private String mealType;
    private float calories;
    private List<String> foodItems;
}
