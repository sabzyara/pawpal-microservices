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
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String serviceCategory;
}

