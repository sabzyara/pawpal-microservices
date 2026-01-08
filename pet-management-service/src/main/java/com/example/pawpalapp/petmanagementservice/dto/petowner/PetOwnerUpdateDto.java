package com.example.pawpalapp.petmanagementservice.dto.petowner;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetOwnerUpdateDto {
    private String username;
    private String phoneNumber;
    private String address;
}
