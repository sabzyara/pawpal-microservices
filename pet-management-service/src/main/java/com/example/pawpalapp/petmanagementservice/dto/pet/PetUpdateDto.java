package com.example.pawpalapp.petmanagementservice.dto.pet;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetUpdateDto {

    private String name;
    private String species;
    private String breed;
    private String gender;
    private int age;
    private int weight;
    private String healthStatus;
}