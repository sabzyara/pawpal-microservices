package com.example.pawpalapp.specialistservice.mapper;

import com.example.pawpalapp.specialistservice.dto.SpecialistCreateDto;
import com.example.pawpalapp.specialistservice.dto.SpecialistResponseDto;
import com.example.pawpalapp.specialistservice.model.Specialist;

public class SpecialistMapper {

    public static Specialist toEntity(SpecialistCreateDto dto) {
        Specialist s = new Specialist();
        s.setUserId(dto.getUserId());
        s.setType(dto.getType());
        return s;
    }

    public static SpecialistResponseDto toDto(Specialist s) {
        return new SpecialistResponseDto(
                s.getId(),
                s.getUserId(),
                s.getType(),
                s.getPhone(),
                s.getDescription()
        );
    }
}
