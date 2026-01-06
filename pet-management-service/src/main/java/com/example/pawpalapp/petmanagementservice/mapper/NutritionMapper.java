package com.example.pawpalapp.petmanagementservice.mapper;

import com.example.pawpalapp.petmanagementservice.dto.nutrition.*;
import com.example.pawpalapp.petmanagementservice.model.NutritionLog;
import org.springframework.stereotype.Component;

@Component
public class NutritionMapper {

    public NutritionLog toEntity(NutritionCreateDto dto) {
        NutritionLog log = new NutritionLog();
        log.setPetId(dto.getPetId());
        log.setDate(dto.getDate());
        log.setMealType(dto.getMealType());
        log.setCalories(dto.getCalories());
        log.setFoodItems(dto.getFoodItems());
        return log;
    }

    public NutritionResponseDto toDto(NutritionLog log) {
        return NutritionResponseDto.builder()
                .logId(log.getLogId())
                .summary(log.generateNutritionSummary())
                .build();
    }
}
