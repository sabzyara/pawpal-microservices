package com.example.pawpalapp.appointmentservice.service;

import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleCreateDto;
import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleResponseDto;
import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.repository.SpecialistScheduleRepository;
import com.example.pawpalapp.appointmentservice.repository.TimeSlotRepository;
import com.example.pawpalapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpecialistScheduleService {

    private final SpecialistScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public SpecialistScheduleResponseDto createOrUpdateSchedule(SpecialistScheduleCreateDto request) {
        Long specialistId = SecurityUtils.getUserId();
        SpecialistType specialistType = resolveSpecialistType(SecurityUtils.getRole());

        validateSchedule(request);

        SpecialistSchedule schedule = scheduleRepository
                .findByUserIdAndSpecialistTypeAndDayOfWeek(specialistId, specialistType, request.getDayOfWeek())
                .map(existing -> updateSchedule(existing, request))
                .orElseGet(() -> createSchedule(specialistId, specialistType, request));

        return toResponseDto(schedule);
    }

    private SpecialistSchedule createSchedule(Long userId, SpecialistType type, SpecialistScheduleCreateDto request) {
        SpecialistSchedule schedule = SpecialistSchedule.builder()
                .userId(userId)
                .specialistType(type)
                .dayOfWeek(request.getDayOfWeek())
                .workStart(request.getWorkStart())
                .workEnd(request.getWorkEnd())
                .breakStart(request.getBreakStart())
                .breakEnd(request.getBreakEnd())
                .slotDurationMinutes(request.getSlotDurationMinutes())
                .build();
        return scheduleRepository.save(schedule);
    }

    private SpecialistSchedule updateSchedule(SpecialistSchedule existing, SpecialistScheduleCreateDto request) {
        existing.setWorkStart(request.getWorkStart());
        existing.setWorkEnd(request.getWorkEnd());
        existing.setBreakStart(request.getBreakStart());
        existing.setBreakEnd(request.getBreakEnd());
        existing.setSlotDurationMinutes(request.getSlotDurationMinutes());
        return scheduleRepository.save(existing);
    }

    @Transactional
    public void regenerateSlotsForDate(LocalDate date) {
        Long userId = SecurityUtils.getUserId();
        SpecialistType specialistType = resolveSpecialistType(SecurityUtils.getRole());

        timeSlotRepository.deleteByUserIdAndSpecialistTypeAndDateAndStatus(
                userId, specialistType, date, SlotStatus.AVAILABLE);

        log.info("Deleted AVAILABLE slots for specialist {} on date {}", userId, date);
    }

    @Transactional
    public void deleteSchedule(DayOfWeek dayOfWeek) {
        Long userId = SecurityUtils.getUserId();
        SpecialistType specialistType = resolveSpecialistType(SecurityUtils.getRole());

        scheduleRepository.deleteByUserIdAndSpecialistTypeAndDayOfWeek(userId, specialistType, dayOfWeek);
        log.info("Deleted schedule for specialist {} on {}", userId, dayOfWeek);
    }

    public List<SpecialistScheduleResponseDto> getMySchedules() {
        Long userId = SecurityUtils.getUserId();
        SpecialistType specialistType = resolveSpecialistType(SecurityUtils.getRole());

        return scheduleRepository.findByUserIdAndSpecialistType(userId, specialistType)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private SpecialistType resolveSpecialistType(String role) {
        return switch (role) {
            case "VET" -> SpecialistType.VET;
            case "SERVICE" -> SpecialistType.SERVICE;
            default -> throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Invalid specialist role: " + role
            );
        };
    }

    private void validateSchedule(SpecialistScheduleCreateDto request) {
        if (!request.getWorkStart().isBefore(request.getWorkEnd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Work start must be before work end");
        }
        if (request.getSlotDurationMinutes() == null || request.getSlotDurationMinutes() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot duration must be positive");
        }
        if (request.getSlotDurationMinutes() > 240) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot duration cannot exceed 4 hours");
        }
        if (request.getBreakStart() != null && request.getBreakEnd() != null) {
            if (!request.getBreakStart().isBefore(request.getBreakEnd())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Break start must be before break end");
            }
            if (request.getBreakStart().isBefore(request.getWorkStart()) ||
                    request.getBreakEnd().isAfter(request.getWorkEnd())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Break must be within work hours");
            }
        }
    }

    private SpecialistScheduleResponseDto toResponseDto(SpecialistSchedule schedule) {
        return SpecialistScheduleResponseDto.builder()
                .id(schedule.getId())
                .dayOfWeek(schedule.getDayOfWeek())
                .workStart(schedule.getWorkStart())
                .workEnd(schedule.getWorkEnd())
                .breakStart(schedule.getBreakStart())
                .breakEnd(schedule.getBreakEnd())
                .slotDurationMinutes(schedule.getSlotDurationMinutes())
                .build();
    }
}