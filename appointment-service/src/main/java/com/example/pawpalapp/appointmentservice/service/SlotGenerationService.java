package com.example.pawpalapp.appointmentservice.service;

import com.example.pawpalapp.appointmentservice.dto.TimeSlotResponseDto;
import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.repository.SpecialistScheduleRepository;
import com.example.pawpalapp.appointmentservice.repository.TimeSlotRepository;
import com.example.pawpalapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlotGenerationService {

    private final SpecialistScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public List<TimeSlotResponseDto> generateSlotsForDate(LocalDate date) {
        Long specialistId = SecurityUtils.getUserId();
        SpecialistType specialistType = resolveSpecialistType(SecurityUtils.getRole());

        if (specialistType != SpecialistType.VET && specialistType != SpecialistType.SERVICE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only specialists can generate slots");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot generate slots for past dates");
        }

        timeSlotRepository.deleteByUserIdAndSpecialistTypeAndDateAndStatus(
                specialistId, specialistType, date, SlotStatus.AVAILABLE);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        SpecialistSchedule schedule = scheduleRepository
                .findByUserIdAndSpecialistTypeAndDayOfWeek(specialistId, specialistType, dayOfWeek)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Schedule not found for " + dayOfWeek + ". Please set up your schedule first."));

        List<TimeSlot> slots = generateSlots(specialistId, specialistType, date, schedule);
        List<TimeSlot> savedSlots = timeSlotRepository.saveAll(slots);

        log.info("Generated {} slots for specialist {} on date {}", savedSlots.size(), specialistId, date);

        return savedSlots.stream()
                .map(this::toResponseDto)
                .toList();
    }

    private List<TimeSlot> generateSlots(Long specialistId, SpecialistType specialistType,
                                         LocalDate date, SpecialistSchedule schedule) {
        List<TimeSlot> slots = new ArrayList<>();
        LocalTime current = schedule.getWorkStart();
        int duration = schedule.getSlotDurationMinutes();

        while (current.plusMinutes(duration).compareTo(schedule.getWorkEnd()) <= 0) {
            LocalTime end = current.plusMinutes(duration);

            boolean isBreakTime = isInBreak(current, end, schedule);

            if (!isBreakTime) {
                slots.add(TimeSlot.builder()
                        .userId(specialistId)
                        .specialistType(specialistType)
                        .date(date)
                        .startTime(current)
                        .endTime(end)
                        .status(SlotStatus.AVAILABLE)
                        .build());
            }
            current = end;
        }
        return slots;
    }

    private boolean isInBreak(LocalTime start, LocalTime end, SpecialistSchedule schedule) {
        if (schedule.getBreakStart() == null || schedule.getBreakEnd() == null) {
            return false;
        }
        return (!start.isBefore(schedule.getBreakStart()) && !end.isAfter(schedule.getBreakEnd())) ||
                (start.isBefore(schedule.getBreakEnd()) && end.isAfter(schedule.getBreakStart()));
    }

    public Page<TimeSlotResponseDto> getAvailableSlots(Long specialistId, SpecialistType specialistType,
                                                       LocalDate date, Pageable pageable) {
        if (date.isBefore(LocalDate.now())) {
            return Page.empty(pageable);
        }

        return timeSlotRepository
                .findByUserIdAndSpecialistTypeAndDateAndStatus(
                        specialistId, specialistType, date, SlotStatus.AVAILABLE, pageable)
                .map(this::toResponseDto);
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

    private TimeSlotResponseDto toResponseDto(TimeSlot slot) {
        return TimeSlotResponseDto.builder()
                .id(slot.getId())
                .userId(slot.getUserId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(slot.getStatus())
                .build();
    }
}