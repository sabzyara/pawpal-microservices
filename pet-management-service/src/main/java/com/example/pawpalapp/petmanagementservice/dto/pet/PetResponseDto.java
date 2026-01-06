package com.example.pawpalapp.petmanagementservice.dto.pet;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetResponseDto {
    private Long id;
    private String name;
    private String species;
    private int age;
    private int weight;
    private String healthStatus;
}
