package com.example.pawpalapp.specialistservice.repository;

import com.example.pawpalapp.specialistservice.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    Optional<ServiceProvider> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}

