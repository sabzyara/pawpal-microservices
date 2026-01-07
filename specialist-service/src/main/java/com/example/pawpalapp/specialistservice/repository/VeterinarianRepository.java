package com.example.pawpalapp.specialistservice.repository;

import com.example.pawpalapp.specialistservice.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {

    Optional<Veterinarian> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

}

