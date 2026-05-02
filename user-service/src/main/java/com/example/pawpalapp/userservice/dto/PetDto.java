package com.example.pawpalapp.userservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDto {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private String gender;
    private int age;
    private int weight;
    private String healthStatus;
    private String avatarUrl;
}
