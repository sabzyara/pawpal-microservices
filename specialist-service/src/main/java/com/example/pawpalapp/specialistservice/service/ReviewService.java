package com.example.pawpalapp.specialistservice.service;

import com.example.pawpalapp.specialistservice.dto.ReviewCreateDto;
import com.example.pawpalapp.specialistservice.model.*;
import com.example.pawpalapp.specialistservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final VeterinarianRepository veterinarianRepository;

    private final ServiceProviderRepository serviceProviderRepository;

    public Review create(
            ReviewCreateDto dto
    ) {

        Jwt jwt = (Jwt)
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        Long userId =
                jwt.getClaim("userId");

        String firstName =
                jwt.getClaim("firstName");

        String lastName =
                jwt.getClaim("lastName");

        Review review = Review.builder()
                .userId(userId)
                .userFirstName(firstName)
                .userLastName(lastName)
                .specialistId(dto.getSpecialistId())
                .specialistType(dto.getSpecialistType())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        updateRating(
                dto.getSpecialistId(),
                dto.getSpecialistType()
        );

        return review;
    }

    public List<Review> getBySpecialist(
            Long specialistId,
            SpecialistType specialistType
    ) {

        return reviewRepository
                .findBySpecialistIdAndSpecialistType(
                        specialistId,
                        specialistType
                );
    }

    private void updateRating(
            Long specialistId,
            SpecialistType specialistType
    ) {

        List<Review> reviews =
                reviewRepository
                        .findBySpecialistIdAndSpecialistType(
                                specialistId,
                                specialistType
                        );

        double avg =
                reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0);

        int count = reviews.size();

        if (specialistType ==
                SpecialistType.VET) {

            Veterinarian vet =
                    veterinarianRepository
                            .findById(specialistId)
                            .orElseThrow();

            vet.setRatingAverage(avg);

            vet.setReviewsCount(count);

            veterinarianRepository.save(vet);

        } else {

            ServiceProvider provider =
                    serviceProviderRepository
                            .findById(specialistId)
                            .orElseThrow();

            provider.setRatingAverage(avg);

            provider.setReviewsCount(count);

            serviceProviderRepository.save(provider);
        }
    }
}