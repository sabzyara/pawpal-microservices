package com.example.pawpalapp.specialistservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VetProfileDto {

    private String licenseNumber;
    private String clinicName;
    private Integer experienceYears;
}

