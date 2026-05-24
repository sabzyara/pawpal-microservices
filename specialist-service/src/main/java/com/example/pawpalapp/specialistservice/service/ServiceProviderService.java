package com.example.pawpalapp.specialistservice.service;

import com.example.pawpalapp.common.storage.FileStorageService;
import com.example.pawpalapp.security.AuthUser;
import com.example.pawpalapp.security.Role;
import com.example.pawpalapp.security.SecurityUtils;
import com.example.pawpalapp.specialistservice.dto.*;
import com.example.pawpalapp.specialistservice.mapper.ServiceProviderMapper;
import com.example.pawpalapp.specialistservice.model.ServiceProvider;
import com.example.pawpalapp.specialistservice.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final FileStorageService fileStorageService;
    private final ServiceProviderMapper serviceProviderMapper;

    @Transactional
    public ServiceProviderResponseDto createMyProfile(ServiceProviderCreateDto request) {
        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.SERVICE) {
            log.warn("User {} with role {} tried to create service provider profile",
                    current.userId(), current.role());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only SERVICE PROVIDER can create profile");
        }

        if (serviceProviderRepository.existsByUserId(current.userId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Profile already exists for user: " + current.userId());
        }

        ServiceProvider serviceProvider = serviceProviderMapper.toEntity(request);
        serviceProvider.setUserId(current.userId());

        ServiceProvider saved = serviceProviderRepository.save(serviceProvider);
        log.info("Created service provider profile for user: {}", current.userId());

        return serviceProviderMapper.toDto(saved);
    }

    public List<ServiceProviderResponseDto> getAll() {
        return serviceProviderRepository.findAll()
                .stream()
                .map(serviceProviderMapper::toDto)
                .toList();
    }

    public ServiceProviderResponseDto getMyProfile() {
        Long userId = SecurityUtils.getUserId();

        ServiceProvider sp = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Profile not found for user: " + userId));

        return serviceProviderMapper.toDto(sp);
    }

    public ServiceProviderResponseDto getById(Long id) {
        ServiceProvider sp = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Service provider not found with id: " + id));

        return serviceProviderMapper.toDto(sp);
    }

    public ServiceProviderResponseDto getByUserId(Long userId) {
        ServiceProvider sp = serviceProviderRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Service provider not found for user id: " + userId));

        return serviceProviderMapper.toDto(sp);
    }

    @Transactional
    public ServiceProviderResponseDto updateMyProfile(ServiceProviderUpdateDto dto) {
        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.SERVICE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only service provider can update profile");
        }

        ServiceProvider sp = serviceProviderRepository
                .findByUserId(current.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Profile not found for user: " + current.userId()));

        serviceProviderMapper.updateEntity(sp, dto);

        ServiceProvider saved = serviceProviderRepository.save(sp);
        log.info("Updated service provider profile for user: {}", current.userId());

        return serviceProviderMapper.toDto(saved);
    }

    @Transactional
    public void deleteMyProfile() {
        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.SERVICE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only service provider can delete profile");
        }

        if (!serviceProviderRepository.existsByUserId(current.userId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Profile not found for user: " + current.userId());
        }

        serviceProviderRepository.deleteByUserId(current.userId());
        log.info("Deleted service provider profile for user: {}", current.userId());
    }

    @Transactional
    public String uploadAvatar(MultipartFile file) {
        Long userId = SecurityUtils.getUserId();

        ServiceProvider sp = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Profile not found for user: " + userId));

        String url = fileStorageService.upload(file);

        sp.setAvatarUrl(url);
        serviceProviderRepository.save(sp);

        log.info("Uploaded avatar for service provider user: {}", userId);
        return url;
    }
}