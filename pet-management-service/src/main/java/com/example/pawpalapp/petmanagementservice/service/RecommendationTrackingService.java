package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.petmanagementservice.client.AiRecommendationClient;
import com.example.pawpalapp.petmanagementservice.dto.ai.*;
import com.example.pawpalapp.petmanagementservice.model.*;
import com.example.pawpalapp.petmanagementservice.repository.*;
import org.springframework.stereotype.Service;

@Service
public class RecommendationTrackingService {

    private final PetRepository petRepository;
    private final ActivityLogRepository activityRepository;
    private final NutritionLogRepository nutritionRepository;
    private final AiRecommendationClient aiClient;

    public RecommendationTrackingService(
            PetRepository petRepository,
            ActivityLogRepository activityRepository,
            NutritionLogRepository nutritionRepository,
            AiRecommendationClient aiClient
    ) {
        this.petRepository = petRepository;
        this.activityRepository = activityRepository;
        this.nutritionRepository = nutritionRepository;
        this.aiClient = aiClient;
    }

    public RecommendationResponseDto getRecommendations(Long petId) {

        // 1️⃣ получаем питомца
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // 2️⃣ суммарная активность
        int totalActivityMinutes =
                activityRepository.findByPetId(petId)
                        .stream()
                        .mapToInt(ActivityLog::getDurationInMinutes)
                        .sum();

        // 3️⃣ суммарные калории
        int totalCalories =
                nutritionRepository.findByPetId(petId)
                        .stream()
                        .mapToInt(n -> (int) n.getCalories())
                        .sum();

        // 4️⃣ формируем запрос в AI
        RecommendationRequestDto request =
                RecommendationRequestDto.builder()
                        .species(pet.getSpecies())
                        .weight(pet.getWeight())
                        .age(pet.getAge())
                        .totalActivityMinutes(totalActivityMinutes)
                        .totalCalories(totalCalories)
                        .build();

        // 5️⃣ отправляем в Python AI
        return aiClient.getRecommendations(request);
    }
}
