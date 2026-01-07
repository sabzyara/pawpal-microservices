package com.example.pawpalapp.specialistservice.mapper;

import com.example.pawpalapp.specialistservice.dto.ServiceProviderCreateDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderResponseDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderUpdateDto;
import com.example.pawpalapp.specialistservice.model.ServiceProvider;

public class ServiceProviderMapper {

    public static ServiceProvider toEntity(ServiceProviderCreateDto dto) {
        ServiceProvider s = new ServiceProvider();
        s.setUserId(dto.getUserId());
        s.setFirstName(dto.getFirstName());
        s.setLastName(dto.getLastName());
        s.setPhoneNumber(dto.getPhoneNumber());
        s.setServiceCategory(dto.getServiceCategory());
        return s;
    }

    public static ServiceProviderResponseDto toDto(ServiceProvider s) {
        return new ServiceProviderResponseDto(
                s.getServiceProviderId(),
                s.getUserId(),
                s.getFirstName(),
                s.getLastName(),
                s.getPhoneNumber(),
                s.getServiceCategory()
        );
    }

    public static void updateEntity(ServiceProvider sp, ServiceProviderUpdateDto dto) {

        if (dto.getFirstName() != null)
            sp.setFirstName(dto.getFirstName());

        if (dto.getLastName() != null)
            sp.setLastName(dto.getLastName());

        if (dto.getPhoneNumber() != null)
            sp.setPhoneNumber(dto.getPhoneNumber());

        if (dto.getServiceCategory() != null)
            sp.setServiceCategory(dto.getServiceCategory());
    }
}
