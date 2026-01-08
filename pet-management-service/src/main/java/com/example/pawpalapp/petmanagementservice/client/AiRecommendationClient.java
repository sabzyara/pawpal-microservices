package com.example.pawpalapp.petmanagementservice.client;

import com.example.pawpalapp.petmanagementservice.dto.ai.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AiRecommendationClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public RecommendationResponseDto getRecommendations(
            RecommendationRequestDto dto
    ) {
        return restTemplate.postForObject(
                aiServiceUrl + "/ai/recommend",
                dto,
                RecommendationResponseDto.class
        );
    }
}
