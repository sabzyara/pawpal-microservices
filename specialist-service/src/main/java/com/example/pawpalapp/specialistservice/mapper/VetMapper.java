package com.example.pawpalapp.specialistservice.mapper;

import com.example.pawpalapp.specialistservice.dto.*;
import com.example.pawpalapp.specialistservice.model.Veterinarian;
import org.springframework.stereotype.Component;

@Component
public class VetMapper {

    public static Veterinarian toEntity(VetCreateDto dto) {
        if (dto == null) return null;

        Veterinarian vet = new Veterinarian();
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setPhoneNumber(dto.getPhoneNumber());
        vet.setLicenseNumber(dto.getLicenseNumber());
        vet.setClinicName(dto.getClinicName());
        vet.setExperienceYears(dto.getExperienceYears());
        vet.setAvatarUrl(dto.getAvatarUrl());
        vet.setPatientsCount(0);
        vet.setAbout(dto.getAbout());
        vet.setEducation(dto.getEducation());
        vet.setPricePerVisit(dto.getPricePerVisit());
        vet.setRating(0.0);
        vet.setAddress(dto.getAddress());
        vet.setCity(dto.getCity());
        return vet;
    }

    public static VetResponseDto toDto(Veterinarian vet) {
        if (vet == null) return null;

        VetResponseDto dto = new VetResponseDto();
        dto.setVetId(vet.getVetId());
        dto.setUserId(vet.getUserId());
        dto.setFirstName(vet.getFirstName());
        dto.setLastName(vet.getLastName());
        dto.setPhoneNumber(vet.getPhoneNumber());
        dto.setLicenseNumber(vet.getLicenseNumber());
        dto.setClinicName(vet.getClinicName());
        dto.setExperienceYears(vet.getExperienceYears());
        dto.setAvatarUrl(vet.getAvatarUrl());
        dto.setPatientsCount(vet.getPatientsCount());
        dto.setAbout(vet.getAbout());
        dto.setEducation(vet.getEducation());
        dto.setPricePerVisit(vet.getPricePerVisit());
        dto.setRating(vet.getRating());
        dto.setAddress(vet.getAddress());
        dto.setCity(vet.getCity());
        return dto;
    }

    public static void updateEntity(Veterinarian vet, VetUpdateDto dto) {
        if (dto == null) return;

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
        if (dto.getAvatarUrl() != null)
            vet.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getAbout() != null)
            vet.setAbout(dto.getAbout());
        if (dto.getEducation() != null)
            vet.setEducation(dto.getEducation());
        if (dto.getPricePerVisit() != null)
            vet.setPricePerVisit(dto.getPricePerVisit());
        if (dto.getAddress() != null)
            vet.setAddress(dto.getAddress());
        if (dto.getCity() != null)
            vet.setCity(dto.getCity());
    }
}