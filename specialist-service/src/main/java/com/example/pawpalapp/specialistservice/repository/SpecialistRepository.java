package com.example.pawpalapp.specialistservice.repository;

import com.example.pawpalapp.specialistservice.model.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {

    Optional<Specialist> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}

