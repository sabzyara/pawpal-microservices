package com.example.pawpalapp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VetDto {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String licenseNumber;
    private String clinicName;
    private Integer experienceYears;
    private String avatarUrl;
    private String about;
    private String education;
    private Double pricePerVisit;
    private String address;
    private String city;

}