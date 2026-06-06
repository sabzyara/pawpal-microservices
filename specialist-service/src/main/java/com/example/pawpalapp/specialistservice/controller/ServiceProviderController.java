package com.example.pawpalapp.specialistservice.controller;

import com.example.pawpalapp.specialistservice.dto.ServiceProviderCreateDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderResponseDto;
import com.example.pawpalapp.specialistservice.dto.ServiceProviderUpdateDto;
import com.example.pawpalapp.specialistservice.service.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/service-providers")
@RequiredArgsConstructor
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    @PostMapping("/me")
    public ServiceProviderResponseDto createMyProfile(@RequestBody ServiceProviderCreateDto request) {
        return serviceProviderService.createMyProfile(request);
    }

    @GetMapping("/me")
    public ServiceProviderResponseDto getMyProfile() {
        return serviceProviderService.getMyProfile();
    }

    @GetMapping
    public ResponseEntity<List<ServiceProviderResponseDto>> getAll() {
        return ResponseEntity.ok(serviceProviderService.getAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ServiceProviderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceProviderService.getById(id));
    }

//    @GetMapping("/user/{userId}")
//    public ResponseEntity<ServiceProviderResponseDto> getByUserId(@PathVariable Long userId) {
//        return ResponseEntity.ok(serviceProviderService.getByUserId(userId));
//    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ServiceProviderResponseDto> getByUserId(
            @PathVariable Long userId
    ) {
        System.out.println("CONTROLLER USER ID = " + userId);

        return ResponseEntity.ok(
                serviceProviderService.getByUserId(userId)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<ServiceProviderResponseDto> update(@RequestBody ServiceProviderUpdateDto dto) {
        return ResponseEntity.ok(serviceProviderService.updateMyProfile(dto));
    }

    @DeleteMapping("/me")
    public void deleteMyProfile() {
        serviceProviderService.deleteMyProfile();
    }

    @PostMapping("/me/avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file) {
        return serviceProviderService.uploadAvatar(file);
    }
}