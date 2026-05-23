package com.example.pawpalapp.specialistservice.service;

import com.example.pawpalapp.common.storage.FileStorageService;
import com.example.pawpalapp.security.AuthUser;
import com.example.pawpalapp.security.Role;
import com.example.pawpalapp.security.SecurityUtils;
import com.example.pawpalapp.specialistservice.dto.VetCreateDto;
import com.example.pawpalapp.specialistservice.dto.VetResponseDto;
import com.example.pawpalapp.specialistservice.dto.VetUpdateDto;
import com.example.pawpalapp.specialistservice.mapper.VetMapper;
import com.example.pawpalapp.specialistservice.model.Veterinarian;
import com.example.pawpalapp.specialistservice.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void createMyProfile(VetCreateDto request) {
        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only VET can update vet profile");
        }

        if (veterinarianRepository.existsByUserId(current.userId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists");
        }

        Veterinarian vet = VetMapper.toEntity(request);
        vet.setUserId(current.userId());

        veterinarianRepository.save(vet);
    }

    public List<VetResponseDto> getAll() {
        return veterinarianRepository.findAll()
                .stream()
                .map(VetMapper::toDto)
                .toList();
    }

    public VetResponseDto getMyProfile() {
        Long userId = SecurityUtils.getUserId();

        Veterinarian vet = veterinarianRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")
                );

        return VetMapper.toDto(vet);
    }

    public VetResponseDto getById(Long id) {
        Veterinarian v = veterinarianRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No veterinarian found"));
        return VetMapper.toDto(v);
    }

    public VetResponseDto getByUserId(Long userId) {
        Veterinarian v = veterinarianRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No veterinarian found"));
        return VetMapper.toDto(v);
    }

    @Transactional
    public VetResponseDto update(VetUpdateDto dto) {
        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets can update vet profile");
        }

        Veterinarian veterinarian = veterinarianRepository
                .findByUserId(current.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian profile not found"));

        VetMapper.updateEntity(veterinarian, dto);
        Veterinarian saved = veterinarianRepository.save(veterinarian);

        return VetMapper.toDto(saved);
    }

    @Transactional
    public void deleteMyProfile() {
        Long userId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (role == null || !role.equals("VET")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets can delete profile");
        }

        if (!veterinarianRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }

        veterinarianRepository.deleteByUserId(userId);
    }

    @Transactional
    public String uploadAvatar(MultipartFile file) {
        Long userId = SecurityUtils.getUserId();

        Veterinarian vet = veterinarianRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        String url = fileStorageService.upload(file);
        vet.setAvatarUrl(url);
        veterinarianRepository.save(vet);

        return url;
    }
}