package com.example.pawpalapp.specialistservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderResponseDto {
    private Long serviceId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String serviceCategory;
    private String avatarUrl;
    private Integer experienceYears;
    private String education;
    private Double rating;
    private String address;
    private String city;
    private Double pricePerVisit;
    private String about;
    private Integer patientsCount;
}

