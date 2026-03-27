package com.example.pawpalapp.petmanagementservice.dto.pet;

import com.example.pawpalapp.petmanagementservice.model.ActivityLog;
import com.example.pawpalapp.petmanagementservice.model.NutritionLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PetFullDto {
    private PetResponseDto pet;
    private List<ActivityLog> activities;
    private List<NutritionLog> nutrition;


}