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
@Table(name = "service_profiles")
public class ServiceWorkerProfile {

    @Id
    private Long specialistId;

    private String serviceCategory;
    private boolean available;
}

