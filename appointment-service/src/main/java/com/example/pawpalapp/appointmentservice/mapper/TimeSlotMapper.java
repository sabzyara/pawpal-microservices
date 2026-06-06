package com.example.pawpalapp.appointmentservice.mapper;

import com.example.pawpalapp.appointmentservice.dto.TimeSlotResponseDto;
import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TimeSlotMapper {

    public TimeSlotResponseDto toResponseDto(TimeSlot timeSlot) {
        if (timeSlot == null) {
            return null;
        }

        return TimeSlotResponseDto.builder()
                .id(timeSlot.getId())
                .specialistId(timeSlot.getSpecialistId())
                .specialistType(timeSlot.getSpecialistType())
                .date(timeSlot.getDate())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .status(timeSlot.getStatus())
                .build();
    }

    public List<TimeSlotResponseDto> toResponseDtoList(List<TimeSlot> timeSlots) {
        if (timeSlots == null || timeSlots.isEmpty()) {
            return new ArrayList<>();
        }
        return timeSlots.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public TimeSlot toEntity(SpecialistSchedule schedule, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (schedule == null || date == null || startTime == null || endTime == null) {
            return null;
        }

        if (startTime.isAfter(endTime)) {
            log.warn("Invalid slot time: start {} is after end {}", startTime, endTime);
            return null;
        }

        return TimeSlot.builder()
                .specialistId(schedule.getSpecialistId())
                .specialistType(schedule.getSpecialistType())
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .status(SlotStatus.AVAILABLE)
                .build();
    }

    public List<TimeSlot> generateSlotsFromSchedule(SpecialistSchedule schedule, LocalDate date) {
        if (schedule == null || date == null) {
            return new ArrayList<>();
        }

        if (date.isBefore(LocalDate.now())) {
            return new ArrayList<>();
        }

        if (schedule.getWorkStart() == null || schedule.getWorkEnd() == null) {
            log.warn("Schedule {} missing work start or end time", schedule.getId());
            return new ArrayList<>();
        }

        if (schedule.getSlotDurationMinutes() == null || schedule.getSlotDurationMinutes() <= 0) {
            log.warn("Schedule {} has invalid slot duration: {}", schedule.getId(), schedule.getSlotDurationMinutes());
            return new ArrayList<>();
        }

        List<TimeSlot> slots = new ArrayList<>();
        LocalTime current = schedule.getWorkStart();
        int duration = schedule.getSlotDurationMinutes();

        while (current.plusMinutes(duration).compareTo(schedule.getWorkEnd()) <= 0) {
            if (isBreakTime(schedule, current, duration)) {
                if (schedule.getBreakEnd() != null) {
                    current = schedule.getBreakEnd();
                    continue;
                }
            }

            TimeSlot slot = toEntity(schedule, date, current, current.plusMinutes(duration));
            if (slot != null) {
                slots.add(slot);
            }
            current = current.plusMinutes(duration);
        }

        log.debug("Generated {} slots for schedule {} on date {}", slots.size(), schedule.getId(), date);
        return slots;
    }

    private boolean isBreakTime(SpecialistSchedule schedule, LocalTime current, int duration) {
        if (schedule.getBreakStart() == null || schedule.getBreakEnd() == null) {
            return false;
        }

        LocalTime slotEnd = current.plusMinutes(duration);
        return slotEnd.isAfter(schedule.getBreakStart()) && current.isBefore(schedule.getBreakEnd());
    }

    public List<TimeSlot> generateSlotsForPeriod(SpecialistSchedule schedule, LocalDate startDate, LocalDate endDate) {
        if (schedule == null || startDate == null || endDate == null) {
            return new ArrayList<>();
        }

        LocalDate actualStart = startDate.isBefore(LocalDate.now()) ? LocalDate.now() : startDate;

        if (actualStart.isAfter(endDate)) {
            return new ArrayList<>();
        }

        List<TimeSlot> allSlots = new ArrayList<>();
        LocalDate currentDate = actualStart;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() == schedule.getDayOfWeek()) {
                allSlots.addAll(generateSlotsFromSchedule(schedule, currentDate));
            }
            currentDate = currentDate.plusDays(1);
        }

        return allSlots;
    }
}