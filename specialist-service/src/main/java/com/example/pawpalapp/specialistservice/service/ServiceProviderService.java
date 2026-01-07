package com.example.pawpalapp.specialistservice.service;

import com.example.pawpalapp.specialistservice.dto.ServiceProviderCreateDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderResponseDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderUpdateDto;
import com.example.pawpalapp.specialistservice.dto.VetResponseDto;
import com.example.pawpalapp.specialistservice.mapper.ServiceProviderMapper;
import com.example.pawpalapp.specialistservice.mapper.VetMapper;
import com.example.pawpalapp.specialistservice.model.ServiceProvider;
import com.example.pawpalapp.specialistservice.model.Veterinarian;
import com.example.pawpalapp.specialistservice.repository.ServiceProviderRepository;
import com.example.pawpalapp.specialistservice.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;


    public ServiceProviderResponseDto create(ServiceProviderCreateDto dto) {

        if (serviceProviderRepository.existsByUserId(dto.getUserId())) {
            throw new RuntimeException("Service provider profile already exists");
        }

        ServiceProvider s = ServiceProviderMapper.toEntity(dto);

        ServiceProvider saved = serviceProviderRepository.save(s);

        return ServiceProviderMapper.toDto(saved);
    }

    public List<ServiceProviderResponseDto> getAll() {
        return serviceProviderRepository.findAll()
                .stream()
                .map(ServiceProviderMapper::toDto)
                .toList();
    }

    public ServiceProviderResponseDto getById(Long id) {
        ServiceProvider sp = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No service provider found"));
        return ServiceProviderMapper.toDto(sp);
    }

    public ServiceProviderResponseDto updateByUserId(Long userId, ServiceProviderUpdateDto dto) {

        ServiceProvider sp = serviceProviderRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));

        ServiceProviderMapper.updateEntity(sp, dto);

        ServiceProvider saved = serviceProviderRepository.save(sp);

        return ServiceProviderMapper.toDto(saved);
    }


    public void delete(Long id) {
        serviceProviderRepository.deleteById(id);
    }
}


