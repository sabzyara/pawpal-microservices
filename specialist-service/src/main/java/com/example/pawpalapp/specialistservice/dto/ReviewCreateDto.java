package com.example.pawpalapp.specialistservice.dto;

import com.example.pawpalapp.specialistservice.model.SpecialistType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateDto {

    private Long specialistId;

    private SpecialistType specialistType;

    private Integer rating;

    private String comment;
}