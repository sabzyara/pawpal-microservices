package com.example.pawpalapp.specialistservice.service;

import com.example.pawpalapp.security.AuthUser;
import com.example.pawpalapp.security.Role;
import com.example.pawpalapp.security.SecurityUtils;
import com.example.pawpalapp.specialistservice.dto.*;
import com.example.pawpalapp.specialistservice.mapper.ServiceProviderMapper;
import com.example.pawpalapp.specialistservice.mapper.VetMapper;
import com.example.pawpalapp.specialistservice.model.ServiceProvider;
import com.example.pawpalapp.specialistservice.model.Veterinarian;
import com.example.pawpalapp.specialistservice.repository.ServiceProviderRepository;
import com.example.pawpalapp.specialistservice.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;

    public void createMyProfile(ServiceProviderCreateDto request) {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = jwt.getClaim("userId");
        String role = jwt.getClaim("role");

        if (!"SERVICE".equals(role)) {
            throw new AccessDeniedException("Only SERVICE PROVIDER can create profile");
        }

        if (serviceProviderRepository.existsByUserId(userId)) {
            throw new RuntimeException("Profile already exists");
        }

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setUserId(userId);
        serviceProvider.setFirstName(request.getFirstName());
        serviceProvider.setLastName(request.getLastName());
        serviceProvider.setPhoneNumber(request.getPhoneNumber());
        serviceProvider.setServiceCategory(request.getServiceCategory());
        serviceProviderRepository.save(serviceProvider);
    }

    public List<ServiceProviderResponseDto> getAll() {
        return serviceProviderRepository.findAll()
                .stream()
                .map(ServiceProviderMapper::toDto)
                .toList();
    }

    public ServiceProviderResponseDto update(ServiceProviderUpdateDto dto) {

        AuthUser current = SecurityUtils.current();

        if (current.role() != Role.SERVICE) {
            throw new AccessDeniedException("Only service provider can update vet profile");
        }

        Long targetUserId = current.userId();

        ServiceProvider serviceProvider = serviceProviderRepository
                .findByUserId(targetUserId)
                .orElseThrow(() -> new RuntimeException("Service provider profile not found"));

        ServiceProviderMapper.updateEntity(serviceProvider, dto);

        ServiceProvider saved = serviceProviderRepository.save(serviceProvider);

        return ServiceProviderMapper.toDto(saved);
    }


    public void delete(Long id) {
        serviceProviderRepository.deleteById(id);
    }
}


