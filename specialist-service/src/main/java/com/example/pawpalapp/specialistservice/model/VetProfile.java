package com.example.pawpalapp.specialistservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vet_profiles")
public class VetProfile {

    @Id
    private Long specialistId;

    private String licenseNumber;
    private String clinicName;
    private int experienceYears;
}
