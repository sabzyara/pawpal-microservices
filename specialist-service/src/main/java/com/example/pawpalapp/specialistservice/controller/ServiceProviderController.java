package com.example.pawpalapp.specialistservice.controller;

import com.example.pawpalapp.specialistservice.dto.ServiceProviderCreateDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderResponseDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderUpdateDto;
import com.example.pawpalapp.specialistservice.dto.VetCreateDto;
import com.example.pawpalapp.specialistservice.repository.VeterinarianRepository;
import com.example.pawpalapp.specialistservice.service.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-providers")
@RequiredArgsConstructor
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    // CREATE SERVICE PROVIDER OF AUTHENTICATED USER ID
    @PostMapping("/me")
    public void createMyProfile(@RequestBody ServiceProviderCreateDto request) {
        serviceProviderService.createMyProfile(request);
    }

    // GET ALL SERVICE PROVIDERS
    @GetMapping
    public ResponseEntity<List<ServiceProviderResponseDto>> getAll() {
        return ResponseEntity.ok(serviceProviderService.getAll());
    }

    // UPDATE SERVICE PROVIDER OF AUTHENTICATED
    @PutMapping("/me")
    public ResponseEntity<ServiceProviderResponseDto> update(
            @RequestBody ServiceProviderUpdateDto dto) {
        return ResponseEntity.ok(serviceProviderService.update(dto));
    }

    //DELETE SERVICE PROVIDER
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        serviceProviderService.delete(id);
    }
}


