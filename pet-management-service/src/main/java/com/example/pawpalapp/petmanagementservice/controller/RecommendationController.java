package com.example.pawpalapp.petmanagementservice.controller;

import com.example.pawpalapp.petmanagementservice.dto.ai.*;
import com.example.pawpalapp.petmanagementservice.service.RecommendationTrackingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationTrackingService service;

    public RecommendationController(RecommendationTrackingService service) {
        this.service = service;
    }

    @GetMapping("/{petId}")
    public RecommendationResponseDto getRecommendations(
            @PathVariable Long petId
    ) {
        return service.getRecommendations(petId);
    }
}
