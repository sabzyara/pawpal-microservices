package com.example.pawpalapp.specialistservice.service;

import com.example.pawpalapp.specialistservice.dto.SpecialistCreateDto;
import com.example.pawpalapp.specialistservice.dto.SpecialistResponseDto;
import com.example.pawpalapp.specialistservice.mapper.SpecialistMapper;
import com.example.pawpalapp.specialistservice.model.ServiceWorkerProfile;
import com.example.pawpalapp.specialistservice.model.Specialist;
import com.example.pawpalapp.specialistservice.model.SpecialistType;
import com.example.pawpalapp.specialistservice.model.VetProfile;
import com.example.pawpalapp.specialistservice.repository.ServiceWorkerProfileRepository;
import com.example.pawpalapp.specialistservice.repository.SpecialistRepository;
import com.example.pawpalapp.specialistservice.repository.VetProfileRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
//@AllArgsConstructor
//public class SpecialistService {
//
//    private final SpecialistRepository specialistRepository;
//    private final VetProfileRepository vetProfileRepository;
//    private final ServiceWorkerProfileRepository serviceWorkerProfileRepository;
//
//    public SpecialistResponseDto create(SpecialistCreateDto dto) {
//
//        if (specialistRepository.existsByUserId(dto.getUserId())) {
//            throw new RuntimeException("Specialist already exists");
//        }
//
//        Specialist specialist = SpecialistMapper.toEntity(dto);
//        Specialist saved = specialistRepository.save(specialist);
//
//        // создаём профиль по типу
//        if (dto.getType() == SpecialistType.VET) {
//            vetProfileRepository.save(
//                    new VetProfile(saved.getId(), null, null, null)
//            );
//        }
//
//        if (dto.getType() == SpecialistType.SERVICE) {
//            serviceWorkerProfileRepository.save(
//                    new ServiceWorkerProfile(saved.getId(), null, true)
//            );
//        }
//
//        return SpecialistMapper.toDto(saved);
//    }
//
//    public SpecialistResponseDto getByUserId(Long userId) {
//        Specialist specialist = specialistRepository.findByUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Specialist not found"));
//        return SpecialistMapper.toDto(specialist);
//    }
//}

@Service
@RequiredArgsConstructor
public class SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final VetProfileRepository vetProfileRepository;
    private final ServiceWorkerProfileRepository serviceWorkerProfileRepository;

    public Specialist create(Long userId, SpecialistType type) {

        Specialist specialist = new Specialist();
        specialist.setUserId(userId);
        specialist.setType(type);

        Specialist saved = specialistRepository.save(specialist);

        if (type == SpecialistType.VET) {
            VetProfile vetProfile = new VetProfile();
            vetProfile.setSpecialistId(saved.getId());
            vetProfileRepository.save(vetProfile);
        }

        if (type == SpecialistType.SERVICE) {
            ServiceWorkerProfile serviceProfile = new ServiceWorkerProfile();
            serviceProfile.setSpecialistId(saved.getId());
            serviceProfile.setAvailable(true);
            serviceWorkerProfileRepository.save(serviceProfile);
        }

        return saved;
    }

    public List<Specialist> getAll() {
        return specialistRepository.findAll();
    }

    public Specialist getById(Long id) {
        return specialistRepository.findById(id)
                .orElseThrow();
    }

    public void delete(Long id) {
        specialistRepository.deleteById(id);
    }
}


