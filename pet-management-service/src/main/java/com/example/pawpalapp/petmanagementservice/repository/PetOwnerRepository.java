package com.example.pawpalapp.petmanagementservice.repository;

import com.example.pawpalapp.petmanagementservice.model.PetOwner;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PetOwnerRepository extends JpaRepository<PetOwner, Long> {

    Optional<PetOwner> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PetOwner p WHERE p.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
