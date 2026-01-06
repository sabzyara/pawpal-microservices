package com.example.pawpalapp.petmanagementservice.dto.petowner;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetOwnerResponseDto {
    private Long id;
    private Long userId;
    private String phoneNumber;
    private String address;
}
