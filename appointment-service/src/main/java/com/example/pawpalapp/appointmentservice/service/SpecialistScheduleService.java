package com.example.pawpalapp.appointmentservice.service;

import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleCreateDto;
import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.repository.SpecialistScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialistScheduleService {

    private final SpecialistScheduleRepository specialistScheduleRepository;

    // CREATE OR UPDATE SCHEDULE
    @Transactional
    public SpecialistSchedule createOrUpdate(
            SpecialistScheduleCreateDto request
    ) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                        .getAuthentication()
                                .getPrincipal();

        Long specialistId = jwt.getClaim("userId");
        String specialistType = jwt.getClaim("role");
        validate(request);

        return specialistScheduleRepository
                .findByUserIdAndSpecialistTypeAndDayOfWeek(
                        specialistId,
                        SpecialistType.valueOf(specialistType),
                        request.getDayOfWeek()
                )
                .map(existing -> update(existing, request))
                .orElseGet(() -> create(
                        specialistId,
                        SpecialistType.valueOf(specialistType),
                        request
                ));
    }


    // CREATE
    private SpecialistSchedule create(
            Long specialistId,
            SpecialistType specialistType,
            SpecialistScheduleCreateDto request
    ) {
        SpecialistSchedule schedule = SpecialistSchedule.builder()
                .userId(specialistId)
                .specialistType(specialistType)
                .dayOfWeek(request.getDayOfWeek())
                .workStart(request.getWorkStart())
                .workEnd(request.getWorkEnd())
                .breakStart(request.getBreakStart())
                .breakEnd(request.getBreakEnd())
                .slotDurationMinutes(request.getSlotDurationMinutes())
                .build();

        return specialistScheduleRepository.save(schedule);
    }

    // UPDATE
    private SpecialistSchedule update(
            SpecialistSchedule existing,
            SpecialistScheduleCreateDto request
    ) {
        existing.setWorkStart(request.getWorkStart());
        existing.setWorkEnd(request.getWorkEnd());
        existing.setBreakStart(request.getBreakStart());
        existing.setBreakEnd(request.getBreakEnd());
        existing.setSlotDurationMinutes(request.getSlotDurationMinutes());

        return specialistScheduleRepository.save(existing);
    }

    public List<SpecialistSchedule> getAll() {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long specialistId = jwt.getClaim("userId");
        String specialistType = jwt.getClaim("role");
        return specialistScheduleRepository.findByUserIdAndSpecialistType(
                specialistId,
                SpecialistType.valueOf(specialistType)
        );
    }

    // CHECKING AND VALIDATING OF TIME
    private void validate(SpecialistScheduleCreateDto r) {

        if (r.getWorkStart().isAfter(r.getWorkEnd())
                || r.getWorkStart().equals(r.getWorkEnd())) {
            throw new IllegalArgumentException(
                    "workStart must be before workEnd");
        }

        if (r.getSlotDurationMinutes() <= 0) {
            throw new IllegalArgumentException(
                    "slotDurationMinutes must be positive");
        }

        if (r.getBreakStart() != null || r.getBreakEnd() != null) {

            if (r.getBreakStart() == null || r.getBreakEnd() == null) {
                throw new IllegalArgumentException(
                        "breakStart and breakEnd must both be set");
            }

            if (r.getBreakStart().isAfter(r.getBreakEnd())
                    || r.getBreakStart().equals(r.getBreakEnd())) {
                throw new IllegalArgumentException(
                        "breakStart must be before breakEnd");
            }

            if (r.getBreakStart().isBefore(r.getWorkStart())
                    || r.getBreakEnd().isAfter(r.getWorkEnd())) {
                throw new IllegalArgumentException(
                        "break must be inside work hours");
            }
        }
    }
}

