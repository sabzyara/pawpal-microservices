package com.example.pawpalapp.specialistservice.repository;

import com.example.pawpalapp.specialistservice.model.ServiceWorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceWorkerProfileRepository extends JpaRepository<ServiceWorkerProfile, Long> {
}

