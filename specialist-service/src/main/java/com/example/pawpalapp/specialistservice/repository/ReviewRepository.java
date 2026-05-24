package com.example.pawpalapp.specialistservice.repository;

import com.example.pawpalapp.specialistservice.model.Review;
import com.example.pawpalapp.specialistservice.model.SpecialistType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review>
    findBySpecialistIdAndSpecialistType(
            Long specialistId,
            SpecialistType specialistType
    );
}
