package com.example.pawpalapp.petmanagementservice.dto.ai;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponseDto {

    private List<String> recommendations;
}
