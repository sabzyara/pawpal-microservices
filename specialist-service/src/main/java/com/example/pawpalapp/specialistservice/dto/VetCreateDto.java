package com.example.pawpalapp.specialistservice.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VetCreateDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String licenseNumber;
    private String clinicName;
    private int experienceYears;
}
