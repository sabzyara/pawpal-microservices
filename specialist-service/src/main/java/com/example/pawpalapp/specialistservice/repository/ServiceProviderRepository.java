package com.example.pawpalapp.specialistservice.repository;

import com.example.pawpalapp.specialistservice.model.ServiceProvider;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    Optional<ServiceProvider> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ServiceProvider s WHERE s.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

