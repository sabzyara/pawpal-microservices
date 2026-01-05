package com.example.pawpalapp.specialistservice.service;

import com.example.pawpalapp.specialistservice.dto.SpecialistCreateDto;
import com.example.pawpalapp.specialistservice.dto.SpecialistResponseDto;
import com.example.pawpalapp.specialistservice.mapper.SpecialistMapper;
import com.example.pawpalapp.specialistservice.model.ServiceWorkerProfile;
import com.example.pawpalapp.specialistservice.model.Specialist;
import com.example.pawpalapp.specialistservice.model.SpecialistType;
import com.example.pawpalapp.specialistservice.model.VetProfile;
import com.example.pawpalapp.specialistservice.repository.ServiceWorkerProfileRepository;
import com.example.pawpalapp.specialistservice.repository.SpecialistRepository;
import com.example.pawpalapp.specialistservice.repository.VetProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final VetProfileRepository vetProfileRepository;
    private final ServiceWorkerProfileRepository serviceWorkerProfileRepository;

    public SpecialistResponseDto create(SpecialistCreateDto dto) {

        if (specialistRepository.existsByUserId(dto.getUserId())) {
            throw new RuntimeException("Specialist already exists");
        }

        Specialist specialist = SpecialistMapper.toEntity(dto);
        Specialist saved = specialistRepository.save(specialist);

        // создаём профиль по типу
        if (dto.getType() == SpecialistType.VET) {
            vetProfileRepository.save(
                    new VetProfile(saved.getId(), null, null, null)
            );
        }

        if (dto.getType() == SpecialistType.SERVICE) {
            serviceWorkerProfileRepository.save(
                    new ServiceWorkerProfile(saved.getId(), null, true)
            );
        }

        return SpecialistMapper.toDto(saved);
    }

    public SpecialistResponseDto getByUserId(Long userId) {
        Specialist specialist = specialistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        return SpecialistMapper.toDto(specialist);
    }
}

