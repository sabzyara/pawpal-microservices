package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.common.storage.FileStorageService;
import com.example.pawpalapp.petmanagementservice.dto.petowner.*;
import com.example.pawpalapp.petmanagementservice.mapper.PetOwnerMapper;
import com.example.pawpalapp.petmanagementservice.model.Pet;
import com.example.pawpalapp.petmanagementservice.model.PetOwner;
import com.example.pawpalapp.petmanagementservice.repository.PetOwnerRepository;
import com.example.pawpalapp.security.AuthUser;
import com.example.pawpalapp.security.Role;
import com.example.pawpalapp.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PetOwnerService {

    private final PetOwnerRepository petOwnerRepository;
    private final FileStorageService fileStorageService;

    public PetOwnerService(PetOwnerRepository repository, FileStorageService fileStorageService) {
        this.petOwnerRepository = repository;
        this.fileStorageService = fileStorageService;
    }

    // CREATE
    public void createMyProfile(PetOwnerCreateDto request) {

        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.OWNER) {
            throw new AccessDeniedException("Only OWNER can create profile");
        }

        Long userId = current.userId();

        if (petOwnerRepository.existsByUserId(userId)) {
            throw new RuntimeException("Profile already exists");
        }

        PetOwner petOwner = new PetOwner();
        petOwner.setUserId(userId);
        petOwner.setUsername(request.getUsername());
        petOwner.setAddress(request.getAddress());
        petOwner.setPhoneNumber(request.getPhoneNumber());

        petOwnerRepository.save(petOwner);
    }

    // UPDATE
    public PetOwnerResponseDto update(PetOwnerUpdateDto dto) {

        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.OWNER && current.role() != Role.ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        Long userId = current.userId();

        PetOwner petOwner = petOwnerRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Pet owner profile not found"));

        PetOwnerMapper.updateEntity(petOwner, dto);

        PetOwner saved = petOwnerRepository.save(petOwner);

        return PetOwnerMapper.toDto(saved);
    }

    // GET
    public PetOwnerResponseDto getMyProfile() {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null) {
                throw new RuntimeException("AUTH IS NULL");
            }

            System.out.println("AUTH: " + auth);
            System.out.println("PRINCIPAL: " + auth.getPrincipal());

            AuthUser current = SecurityUtils.current();

            if (current == null) {
                throw new RuntimeException("CURRENT USER NULL");
            }

            Long userId = current.userId();

            System.out.println("USER ID: " + userId);

            PetOwner owner = petOwnerRepository
                    .findByUserId(userId)
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")
                    );

            return PetOwnerMapper.toDto(owner);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void deleteByUserId(Long userId) {
        petOwnerRepository.deleteByUserId(userId);
    }

    public String uploadAvatar(MultipartFile file) {

        System.out.println("🔥 START UPLOAD");
        System.out.println("FILE: " + file);

        AuthUser current = SecurityUtils.current();
        System.out.println("USER ID: " + current.userId());

        PetOwner owner = petOwnerRepository
                .findByUserId(current.userId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        System.out.println("OWNER FOUND");

        String url = fileStorageService.upload(file);

        System.out.println("URL: " + url);

        owner.setAvatarUrl(url);
        petOwnerRepository.save(owner);

        return url;
    }

    public PetOwnerResponseDto getById(Long id) {
        PetOwner po = petOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No pet owner found"));
        return PetOwnerMapper.toDto(po);
    }

    public PetOwnerResponseDto getByUserId(Long userId) {
        PetOwner po = petOwnerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No pet owner found"));

        return PetOwnerMapper.toDto(po);
    }
}