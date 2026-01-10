package com.example.pawpalapp.specialistservice.service;


import com.example.pawpalapp.security.AuthUser;
import com.example.pawpalapp.security.Role;
import com.example.pawpalapp.security.SecurityUtils;
import com.example.pawpalapp.specialistservice.dto.VetCreateDto;
import com.example.pawpalapp.specialistservice.dto.VetResponseDto;
import com.example.pawpalapp.specialistservice.dto.VetUpdateDto;
import com.example.pawpalapp.specialistservice.mapper.VetMapper;
import com.example.pawpalapp.specialistservice.model.ServiceProvider;
import com.example.pawpalapp.specialistservice.model.Veterinarian;
import com.example.pawpalapp.specialistservice.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;

    public void createMyProfile(VetCreateDto request) {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = jwt.getClaim("userId");
        String role = jwt.getClaim("role");

        if (!"VET".equals(role)) {
            throw new AccessDeniedException("Only VET can create profile");
        }

        if (veterinarianRepository.existsByUserId(userId)) {
            throw new RuntimeException("Profile already exists");
        }

        Veterinarian vet = new Veterinarian();
        vet.setUserId(userId);
        vet.setFirstName(request.getFirstName());
        vet.setLastName(request.getLastName());
        vet.setPhoneNumber(request.getPhoneNumber());
        vet.setLicenseNumber(request.getLicenseNumber());
        vet.setExperienceYears(request.getExperienceYears());
        vet.setClinicName(request.getClinicName());
        veterinarianRepository.save(vet);
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

    public VetResponseDto update(VetUpdateDto dto) {

        AuthUser current = SecurityUtils.current();

        // RBAC
        if (current.role() != Role.VET) {
            throw new AccessDeniedException("Only vets can update vet profile");
        }

        Long targetUserId = current.userId();

        Veterinarian veterinarian = veterinarianRepository
                .findByUserId(targetUserId)
                .orElseThrow(() -> new RuntimeException("Veterinarian profile not found"));

        VetMapper.updateEntity(veterinarian, dto);

        Veterinarian saved = veterinarianRepository.save(veterinarian);

        return VetMapper.toDto(saved);
    }


    public void delete(Long id) {
        veterinarianRepository.deleteById(id);
    }
}
