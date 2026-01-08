package com.example.pawpalapp.petmanagementservice.mapper;

import com.example.pawpalapp.petmanagementservice.dto.petowner.*;
import com.example.pawpalapp.petmanagementservice.model.PetOwner;
import org.springframework.stereotype.Component;

@Component
public class PetOwnerMapper {

    public static PetOwner toEntity(PetOwnerCreateDto dto) {
        return PetOwner.builder()
                .userId(dto.getUserId())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .build();
    }

    public static PetOwnerResponseDto toDto(PetOwner owner) {
        return PetOwnerResponseDto.builder()
                .id(owner.getId())
                .userId(owner.getUserId())
                .phoneNumber(owner.getPhoneNumber())
                .address(owner.getAddress())
                .build();
    }


    public static void updateEntity(PetOwner petOwner, PetOwnerUpdateDto dto) {

        if (dto.getUsername() != null)
            petOwner.setUsername(dto.getUsername());

        if (dto.getAddress() != null)
            petOwner.setAddress(dto.getAddress());

        if (dto.getPhoneNumber() != null)
            petOwner.setPhoneNumber(dto.getPhoneNumber());

    }
}
