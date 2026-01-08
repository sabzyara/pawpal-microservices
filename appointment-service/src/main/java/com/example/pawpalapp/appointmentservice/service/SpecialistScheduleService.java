package com.example.pawpalapp.appointmentservice.service;

import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleCreateDto;
import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.repository.SpecialistScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialistScheduleService {

    private final SpecialistScheduleRepository specialistScheduleRepository;

    @Transactional
    public SpecialistSchedule createOrUpdate(
            Long specialistId,
            SpecialistType specialistType,
            SpecialistScheduleCreateDto request
    ) {

        validate(request);

        return specialistScheduleRepository
                .findByUserIdAndSpecialistTypeAndDayOfWeek(
                        specialistId,
                        specialistType,
                        request.getDayOfWeek()
                )
                .map(existing -> update(existing, request))
                .orElseGet(() -> create(
                        specialistId,
                        specialistType,
                        request
                ));
    }


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

    public List<SpecialistSchedule> getAll(
            Long specialistId,
            SpecialistType specialistType
    ) {
        return specialistScheduleRepository.findByUserIdAndSpecialistType(
                specialistId,
                specialistType
        );
    }


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

