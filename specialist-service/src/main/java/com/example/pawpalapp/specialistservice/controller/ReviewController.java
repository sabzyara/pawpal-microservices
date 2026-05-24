package com.example.pawpalapp.specialistservice.controller;

import com.example.pawpalapp.specialistservice.dto.ReviewCreateDto;
import com.example.pawpalapp.specialistservice.model.Review;
import com.example.pawpalapp.specialistservice.model.SpecialistType;
import com.example.pawpalapp.specialistservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(
            @RequestBody ReviewCreateDto dto
    ) {
        return reviewService.create(dto);
    }

    @GetMapping
    public List<Review> getBySpecialist(
            @RequestParam Long specialistId,

            @RequestParam
            SpecialistType specialistType
    ) {
        return reviewService.getBySpecialist(
                specialistId,
                specialistType
        );
    }
}