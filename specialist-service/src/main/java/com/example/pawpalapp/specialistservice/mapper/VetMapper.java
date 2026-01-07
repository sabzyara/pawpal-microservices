package com.example.pawpalapp.specialistservice.mapper;

import com.example.pawpalapp.specialistservice.dto.*;
import com.example.pawpalapp.specialistservice.model.ServiceProvider;
import com.example.pawpalapp.specialistservice.model.Veterinarian;

public class VetMapper {

    public static Veterinarian toEntity(VetCreateDto dto) {
        Veterinarian s = new Veterinarian();
        s.setUserId(dto.getUserId());
        s.setFirstName(dto.getFirstName());
        s.setLastName(dto.getLastName());
        s.setPhoneNumber(dto.getPhoneNumber());
        s.setLicenseNumber(dto.getLicenseNumber());
        s.setClinicName(dto.getClinicName());
        s.setExperienceYears(dto.getExperienceYears());
        return s;
    }

    public static VetResponseDto toDto(Veterinarian s) {
        return new VetResponseDto(
                s.getVetId(),
                s.getUserId(),
                s.getFirstName(),
                s.getLastName(),
                s.getPhoneNumber(),
                s.getLicenseNumber(),
                s.getClinicName(),
                s.getExperienceYears()
        );
    }

    public static void updateEntity(Veterinarian vet, VetUpdateDto dto) {

        if (dto.getFirstName() != null)
            vet.setFirstName(dto.getFirstName());

        if (dto.getLastName() != null)
            vet.setLastName(dto.getLastName());

        if (dto.getPhoneNumber() != null)
            vet.setPhoneNumber(dto.getPhoneNumber());

        if (dto.getLicenseNumber() != null)
            vet.setLicenseNumber(dto.getLicenseNumber());

        if (dto.getClinicName() != null)
            vet.setClinicName(dto.getClinicName());

        if (dto.getExperienceYears() != null)
            vet.setExperienceYears(dto.getExperienceYears());
    }
}
