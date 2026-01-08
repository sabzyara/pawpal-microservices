package com.example.pawpalapp.specialistservice.controller;

import com.example.pawpalapp.specialistservice.dto.*;
import com.example.pawpalapp.specialistservice.service.ServiceProviderService;
import com.example.pawpalapp.specialistservice.service.VeterinarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarians")
@RequiredArgsConstructor
public class VeterinarianController {

    private final VeterinarianService veterinarianService;

    // CREATE VET OF AUTHENTICATED USER ID
    @PostMapping("/me")
    public void createMyProfile(@RequestBody VetCreateDto request) {
        veterinarianService.createMyProfile(request);
    }

    // GET ALL VETS
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> getAll() {
        return ResponseEntity.ok(veterinarianService.getAll());
    }

    // UPDATE VET BY AUTHENTICATED ID
    @PutMapping("/me")
    public ResponseEntity<VetResponseDto> update(@RequestBody VetUpdateDto dto) {

        return ResponseEntity.ok(veterinarianService.update(dto));
    }

    // DELETE VET
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        veterinarianService.delete(id);
    }
}
