package com.example.pawpalapp.specialistservice.mapper;

import com.example.pawpalapp.specialistservice.dto.*;
import com.example.pawpalapp.specialistservice.model.ServiceProvider;
import org.springframework.stereotype.Component;


@Component
public class ServiceProviderMapper {

    public ServiceProvider toEntity(ServiceProviderCreateDto dto) {
        if (dto == null) return null;

        ServiceProvider sp = new ServiceProvider();
        sp.setFirstName(dto.getFirstName());
        sp.setLastName(dto.getLastName());
        sp.setPhoneNumber(dto.getPhoneNumber());
        sp.setServiceCategory(dto.getServiceCategory());
        sp.setExperienceYears(dto.getExperienceYears());
        sp.setEducation(dto.getEducation());
        sp.setAddress(dto.getAddress());
        sp.setCity(dto.getCity());
        sp.setPricePerVisit(dto.getPricePerVisit());
        sp.setAbout(dto.getAbout());
        sp.setRatingAverage(0.0);
        sp.setReviewsCount(0);
        sp.setPatientsCount(0);
        return sp;
    }

    public ServiceProviderResponseDto toDto(ServiceProvider sp) {
        if (sp == null) return null;

        ServiceProviderResponseDto dto = new ServiceProviderResponseDto();
        dto.setServiceId(sp.getServiceId());
        dto.setUserId(sp.getUserId());
        dto.setFirstName(sp.getFirstName());
        dto.setLastName(sp.getLastName());
        dto.setPhoneNumber(sp.getPhoneNumber());
        dto.setServiceCategory(sp.getServiceCategory());
        dto.setAvatarUrl(sp.getAvatarUrl());
        dto.setExperienceYears(sp.getExperienceYears());
        dto.setEducation(sp.getEducation());
        dto.setRating(sp.getRatingAverage());
        dto.setAddress(sp.getAddress());
        dto.setCity(sp.getCity());
        dto.setPricePerVisit(sp.getPricePerVisit());
        dto.setAbout(sp.getAbout());
        dto.setPatientsCount(sp.getPatientsCount());
        return dto;
    }

    public void updateEntity(ServiceProvider sp, ServiceProviderUpdateDto dto) {
        if (dto == null) return;

        if (dto.getFirstName() != null)
            sp.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)
            sp.setLastName(dto.getLastName());
        if (dto.getPhoneNumber() != null)
            sp.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getServiceCategory() != null)
            sp.setServiceCategory(dto.getServiceCategory());
        if (dto.getExperienceYears() != null)
            sp.setExperienceYears(dto.getExperienceYears());
        if (dto.getEducation() != null)
            sp.setEducation(dto.getEducation());
        if (dto.getAvatarUrl() != null)
            sp.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getAddress() != null)
            sp.setAddress(dto.getAddress());
        if (dto.getCity() != null)
            sp.setCity(dto.getCity());
        if (dto.getPricePerVisit() != null)
            sp.setPricePerVisit(dto.getPricePerVisit());
        if (dto.getAbout() != null)
            sp.setAbout(dto.getAbout());
    }
}