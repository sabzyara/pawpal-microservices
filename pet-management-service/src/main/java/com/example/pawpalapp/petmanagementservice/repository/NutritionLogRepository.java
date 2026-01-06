package com.example.pawpalapp.petmanagementservice.repository;

import com.example.pawpalapp.petmanagementservice.model.NutritionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NutritionLogRepository extends JpaRepository<NutritionLog, Long> {

    List<NutritionLog> findByPetId(Long petId);
}
