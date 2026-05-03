package com.example.pawpalapp.userservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerFullDto {
    private PetOwnerDto owner;
    private List<PetDto> pets;

}