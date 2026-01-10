package com.example.pawpalapp.appointmentservice.service;

import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import com.example.pawpalapp.appointmentservice.repository.SpecialistScheduleRepository;
import com.example.pawpalapp.appointmentservice.repository.TimeSlotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotGenerationService {

    private final SpecialistScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public List<TimeSlot> generateSlotsForDate(LocalDate date) {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long specialistId = jwt.getClaim("userId");
        String specialistType = jwt.getClaim("role");

        // CHECKING IF SLOTS ARE ALREADY GENERATED
        if (timeSlotRepository.existsByUserIdAndSpecialistTypeAndDate(
                specialistId, SpecialistType.valueOf(specialistType), date)) {

            return timeSlotRepository
                    .findByUserIdAndSpecialistTypeAndDate(
                            specialistId, SpecialistType.valueOf(specialistType), date);
        }

        // GET SCHEDULE OF THIS DAY OF WEEK
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        SpecialistSchedule schedule = scheduleRepository
                .findByUserIdAndSpecialistTypeAndDayOfWeek(
                        specialistId, SpecialistType.valueOf(specialistType), dayOfWeek)
                .orElseThrow(() ->
                        new IllegalStateException("Schedule not found for this day"));

        // GENERATE SLOTS
        List<TimeSlot> slots = new ArrayList<>();

        LocalTime current = schedule.getWorkStart();
        int duration = schedule.getSlotDurationMinutes();

        while (current.plusMinutes(duration)
                .isBefore(schedule.getWorkEnd())
                || current.plusMinutes(duration)
                .equals(schedule.getWorkEnd())) {

            LocalTime end = current.plusMinutes(duration);

            boolean isBreak =
                    schedule.getBreakStart() != null &&
                            schedule.getBreakEnd() != null &&
                            !current.isBefore(schedule.getBreakStart()) &&
                            current.isBefore(schedule.getBreakEnd());

            if (!isBreak) {
                slots.add(TimeSlot.builder()
                        .userId(specialistId)
                        .specialistType(SpecialistType.valueOf(specialistType))
                        .date(date)
                        .startTime(current)
                        .endTime(end)
                        .status(SlotStatus.AVAILABLE)
                        .build());
            }

            current = end;
        }

        return timeSlotRepository.saveAll(slots);
    }
}

