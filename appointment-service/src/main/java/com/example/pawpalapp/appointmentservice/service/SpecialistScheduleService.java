package com.example.pawpalapp.appointmentservice.service;

import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleCreateDto;
import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleResponseDto;
import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleUpdateDto;
import com.example.pawpalapp.appointmentservice.mapper.SpecialistScheduleMapper;
import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpecialistScheduleService {

    private final SpecialistScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final SpecialistScheduleMapper scheduleMapper;
    private final SlotGenerationService slotGenerationService;

    @Transactional
    public SpecialistScheduleResponseDto createSchedule(SpecialistScheduleCreateDto request) {
        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();
        SpecialistType specialistType = resolveSpecialistType(role);

        if (!request.getSpecialistId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create schedule for yourself");
        }

        if (request.getSpecialistType() != specialistType && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specialist type does not match your role");
        }

        validateSchedule(request);

        if (scheduleRepository.existsBySpecialistIdAndSpecialistTypeAndDayOfWeek(
                request.getSpecialistId(), request.getSpecialistType(), request.getDayOfWeek())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Schedule already exists for %s on %s",
                            request.getSpecialistType(), request.getDayOfWeek()));
        }

        SpecialistSchedule schedule = scheduleMapper.toEntity(request);
        schedule = scheduleRepository.save(schedule);

        slotGenerationService.regenerateSlotsForSchedule(schedule);

        log.info("Created schedule for specialist {} on {}", schedule.getSpecialistId(), schedule.getDayOfWeek());

        return scheduleMapper.toResponseDto(schedule);
    }

    @Transactional
    public SpecialistScheduleResponseDto updateSchedule(Long id, SpecialistScheduleUpdateDto request) {
        SpecialistSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!schedule.getSpecialistId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own schedule");
        }

        validateUpdate(request);

        scheduleMapper.updateEntity(schedule, request);
        schedule = scheduleRepository.save(schedule);

        // Делегируем перегенерацию слотов в SlotGenerationService
        slotGenerationService.regenerateSlotsForSchedule(schedule);

        log.info("Updated schedule {} for specialist {}", id, schedule.getSpecialistId());

        return scheduleMapper.toResponseDto(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        SpecialistSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!schedule.getSpecialistId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own schedule");
        }

        scheduleRepository.delete(schedule);
        log.info("Deleted schedule {} for specialist {}", id, schedule.getSpecialistId());
    }

    @Transactional
    public void deleteScheduleByDay(DayOfWeek dayOfWeek) {
        Long specialistId = SecurityUtils.getUserId();
        SpecialistType specialistType = resolveSpecialistType(SecurityUtils.getRole());

        if (!scheduleRepository.existsBySpecialistIdAndSpecialistTypeAndDayOfWeek(
                specialistId, specialistType, dayOfWeek)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("No schedule found for %s on %s", specialistType, dayOfWeek));
        }

        scheduleRepository.deleteBySpecialistIdAndSpecialistTypeAndDayOfWeek(specialistId, specialistType, dayOfWeek);
        log.info("Deleted schedule for specialist {} on {}", specialistId, dayOfWeek);
    }


    public List<SpecialistScheduleResponseDto> getMySchedules() {
        Long specialistId = SecurityUtils.getUserId();
        SpecialistType specialistType = resolveSpecialistType(SecurityUtils.getRole());

        List<SpecialistSchedule> schedules = scheduleRepository
                .findBySpecialistIdAndSpecialistType(specialistId, specialistType);

        return schedules.stream()
                .map(scheduleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public SpecialistScheduleResponseDto getScheduleById(Long id) {
        SpecialistSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        boolean isOwner = schedule.getSpecialistId().equals(currentUserId);
        if (!isOwner && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return scheduleMapper.toResponseDto(schedule);
    }

    public SpecialistScheduleResponseDto getScheduleByDay(DayOfWeek dayOfWeek) {
        Long specialistId = SecurityUtils.getUserId();
        SpecialistType specialistType = resolveSpecialistType(SecurityUtils.getRole());

        SpecialistSchedule schedule = scheduleRepository
                .findBySpecialistIdAndSpecialistTypeAndDayOfWeek(specialistId, specialistType, dayOfWeek)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No schedule found for %s on %s", specialistType, dayOfWeek)));

        return scheduleMapper.toResponseDto(schedule);
    }

    public List<SpecialistScheduleResponseDto> getSchedulesBySpecialist(Long specialistId) {
        String role = SecurityUtils.getRole();
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can view other specialists' schedules");
        }

        List<SpecialistSchedule> schedules = scheduleRepository.findBySpecialistId(specialistId);
        return schedules.stream()
                .map(scheduleMapper::toResponseDto)
                .collect(Collectors.toList());
    }


    private void validateSchedule(SpecialistScheduleCreateDto request) {
        if (request.getWorkStart() == null || request.getWorkEnd() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Work start and end times are required");
        }
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

    private void validateUpdate(SpecialistScheduleUpdateDto request) {
        if (request.getWorkStart() != null && request.getWorkEnd() != null) {
            if (!request.getWorkStart().isBefore(request.getWorkEnd())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Work start must be before work end");
            }
        }
        if (request.getSlotDurationMinutes() != null) {
            if (request.getSlotDurationMinutes() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot duration must be positive");
            }
            if (request.getSlotDurationMinutes() > 240) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot duration cannot exceed 4 hours");
            }
        }
    }

    private SpecialistType resolveSpecialistType(String role) {
        String upperRole = role.toUpperCase();
        return switch (upperRole) {
            case "VET" -> SpecialistType.VET;
            case "SERVICE" -> SpecialistType.SERVICE;
            case "ADMIN" -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Admin must specify specialist type in request"
            );
            default -> throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Invalid specialist role: " + role
            );
        };
    }
}