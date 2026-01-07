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

    @PostMapping
    public ResponseEntity<VetResponseDto> create(@RequestBody VetCreateDto dto,
                                                 ) {
        return ResponseEntity.ok(veterinarianService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<VetResponseDto>> getAll() {
        return ResponseEntity.ok(veterinarianService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VetResponseDto> update(
            @PathVariable Long id,
            @RequestBody VetUpdateDto dto) {

        return ResponseEntity.ok(veterinarianService.updateByUserId(id, dto));
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        veterinarianService.delete(id);
    }
}
