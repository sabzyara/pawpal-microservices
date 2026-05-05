package com.example.pawpalapp.petmanagementservice.service;

import com.example.pawpalapp.petmanagementservice.dto.pet.BreedDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BreedService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String DOG_API_KEY = "live_ujrHaZZNQaJHvCTdiW57DpFPySYbDpycZgYKxVumnedpwrM5jAnrI4M2bCTuWl2S";
    private final String CAT_API_KEY = "live_cXA3Tc7sLUEW7fE3P8yAQlvVOvg0tjcFwfusLKBDnUGR9VdB9rONeXKrTeXZ7F5C";

    public List<BreedDto> getBreeds(String species) {

        String url;
        String apiKey;

        if ("dog".equalsIgnoreCase(species)) {
            url = "https://api.thedogapi.com/v1/breeds";
            apiKey = DOG_API_KEY;
        } else if ("cat".equalsIgnoreCase(species)) {
            url = "https://api.thecatapi.com/v1/breeds";
            apiKey = CAT_API_KEY;
        } else {
            throw new IllegalArgumentException("Unknown species");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody().stream().map(b -> {
            BreedDto dto = new BreedDto();
            dto.setId(String.valueOf(b.get("id")));
            dto.setName((String) b.get("name"));
            return dto;
        }).toList();
    }
}