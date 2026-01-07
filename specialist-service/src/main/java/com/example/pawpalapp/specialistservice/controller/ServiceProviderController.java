package com.example.pawpalapp.specialistservice.controller;

import com.example.pawpalapp.specialistservice.dto.ServiceProviderCreateDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderResponseDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderUpdateDto;
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

    @PostMapping
    public ResponseEntity<ServiceProviderResponseDto> create(@RequestBody ServiceProviderCreateDto dto) {
        return ResponseEntity.ok(serviceProviderService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ServiceProviderResponseDto>> getAll() {
        return ResponseEntity.ok(serviceProviderService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceProviderResponseDto> update(
            @PathVariable Long id,
            @RequestBody ServiceProviderUpdateDto dto) {

        return ResponseEntity.ok(serviceProviderService.updateByUserId(id, dto));
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        serviceProviderService.delete(id);
    }
}


