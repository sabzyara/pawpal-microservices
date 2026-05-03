package com.example.pawpalapp.userservice.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetOwnerDto {
    private Long id;
    private Long userId;
    private String username;
    private String phoneNumber;
    private String address;
}
