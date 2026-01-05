package com.example.pawpalapp.specialistservice.dto;

import com.example.pawpalapp.specialistservice.model.SpecialistType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistCreateDto {

    private Long userId;
    private SpecialistType type;
}

