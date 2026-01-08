package com.example.pawpalapp.petmanagementservice.repository;

import com.example.pawpalapp.petmanagementservice.model.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetOwnerRepository extends JpaRepository<PetOwner, Long> {

    Optional<PetOwner> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
