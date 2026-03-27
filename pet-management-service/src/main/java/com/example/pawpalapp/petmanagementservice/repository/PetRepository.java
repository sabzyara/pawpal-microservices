package com.example.pawpalapp.petmanagementservice.repository;

import com.example.pawpalapp.petmanagementservice.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByOwnerId(Long ownerId);
    Optional<Pet> findFirstByOwnerId(Long ownerId);
}
