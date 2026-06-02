package com.example.pawpalapp.appointmentservice.mapper;

import com.example.pawpalapp.appointmentservice.dto.TimeSlotResponseDto;
import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                .isAvailable(timeSlot.isAvailable())
                .build();
    }

    public List<TimeSlotResponseDto> toResponseDtoList(List<TimeSlot> timeSlots) {
        if (timeSlots == null) {
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
        if (schedule.getWorkStart() == null || schedule.getWorkEnd() == null) {
            return new ArrayList<>();
        }
        if (schedule.getSlotDurationMinutes() == null || schedule.getSlotDurationMinutes() <= 0) {
            return new ArrayList<>();
        }

        List<TimeSlot> slots = new ArrayList<>();
        LocalTime current = schedule.getWorkStart();
        int duration = schedule.getSlotDurationMinutes();

        while (current.plusMinutes(duration).compareTo(schedule.getWorkEnd()) <= 0) {
            if (isBreakTime(schedule, current, duration)) {
                current = schedule.getBreakEnd();
                continue;
            }
            TimeSlot slot = toEntity(schedule, date, current, current.plusMinutes(duration));
            if (slot != null) {
                slots.add(slot);
            }
            current = current.plusMinutes(duration);
        }
        return slots;
    }

    private boolean isBreakTime(SpecialistSchedule schedule, LocalTime current, int duration) {
        if (schedule.getBreakStart() == null || schedule.getBreakEnd() == null) {
            return false;
        }
        return current.isBefore(schedule.getBreakEnd()) &&
                current.plusMinutes(duration).isAfter(schedule.getBreakStart());
    }

    public List<TimeSlot> generateSlotsForPeriod(SpecialistSchedule schedule, LocalDate startDate, LocalDate endDate) {
        if (schedule == null || startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        List<TimeSlot> allSlots = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() == schedule.getDayOfWeek()) {
                allSlots.addAll(generateSlotsFromSchedule(schedule, currentDate));
            }
            currentDate = currentDate.plusDays(1);
        }
        return allSlots;
    }
}