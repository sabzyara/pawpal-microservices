package com.example.pawpalapp.specialistservice.service;


import com.example.pawpalapp.specialistservice.dto.VetCreateDto;
import com.example.pawpalapp.specialistservice.dto.VetResponseDto;
import com.example.pawpalapp.specialistservice.dto.VetUpdateDto;
import com.example.pawpalapp.specialistservice.mapper.VetMapper;
import com.example.pawpalapp.specialistservice.model.Veterinarian;
import com.example.pawpalapp.specialistservice.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;

    public VetResponseDto create(VetCreateDto dto) {

        if (veterinarianRepository.existsByUserId(dto.getUserId())) {
            throw new RuntimeException("Veterinarian profile already exists");
        }

        Veterinarian vet = VetMapper.toEntity(dto);

        Veterinarian saved = veterinarianRepository.save(vet);

        return VetMapper.toDto(saved);

    }

    public List<VetResponseDto> getAll() {
        return veterinarianRepository.findAll()
                .stream()
                .map(VetMapper::toDto)
                .toList();
    }

    public VetResponseDto getById(Long id) {
        Veterinarian v = veterinarianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No veterinarian found"));
        return VetMapper.toDto(v);
    }

    public VetResponseDto updateByUserId(Long userId, VetUpdateDto dto) {

        Veterinarian vet = veterinarianRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Veterinarian profile not found"));

        VetMapper.updateEntity(vet, dto);

        Veterinarian saved = veterinarianRepository.save(vet);

        return VetMapper.toDto(saved);
    }



    public void delete(Long id) {
        veterinarianRepository.deleteById(id);
    }
}
