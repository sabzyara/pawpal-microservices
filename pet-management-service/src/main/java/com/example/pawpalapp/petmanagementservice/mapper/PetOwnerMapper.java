package com.example.pawpalapp.petmanagementservice.mapper;

import com.example.pawpalapp.petmanagementservice.dto.petowner.*;
import com.example.pawpalapp.petmanagementservice.model.PetOwner;
import org.springframework.stereotype.Component;

@Component
public class PetOwnerMapper {

    public PetOwner toEntity(PetOwnerCreateDto dto) {
        return PetOwner.builder()
                .userId(dto.getUserId())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .build();
    }

    public PetOwnerResponseDto toDto(PetOwner owner) {
        return PetOwnerResponseDto.builder()
                .id(owner.getId())
                .userId(owner.getUserId())
                .phoneNumber(owner.getPhoneNumber())
                .address(owner.getAddress())
                .build();
    }
}
