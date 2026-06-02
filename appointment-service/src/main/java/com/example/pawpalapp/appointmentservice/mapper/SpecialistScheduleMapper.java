package com.example.pawpalapp.appointmentservice.mapper;
import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleCreateDto;
import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleResponseDto;
import com.example.pawpalapp.appointmentservice.dto.SpecialistScheduleUpdateDto;
import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import org.springframework.stereotype.Component;

@Component
public class SpecialistScheduleMapper {

    public SpecialistSchedule toEntity(SpecialistScheduleCreateDto dto) {
        if (dto == null) {
            return null;
        }

        return SpecialistSchedule.builder()
                .specialistId(dto.getSpecialistId())
                .specialistType(dto.getSpecialistType())
                .dayOfWeek(dto.getDayOfWeek())
                .workStart(dto.getWorkStart())
                .workEnd(dto.getWorkEnd())
                .breakStart(dto.getBreakStart())
                .breakEnd(dto.getBreakEnd())
                .slotDurationMinutes(dto.getSlotDurationMinutes())
                .build();
    }

    public SpecialistScheduleResponseDto toResponseDto(SpecialistSchedule schedule) {
        if (schedule == null) {
            return null;
        }

        return SpecialistScheduleResponseDto.builder()
                .id(schedule.getId())
                .specialistId(schedule.getSpecialistId())
                .specialistType(schedule.getSpecialistType())
                .dayOfWeek(schedule.getDayOfWeek())
                .workStart(schedule.getWorkStart())
                .workEnd(schedule.getWorkEnd())
                .breakStart(schedule.getBreakStart())
                .breakEnd(schedule.getBreakEnd())
                .slotDurationMinutes(schedule.getSlotDurationMinutes())
                .build();
    }


    public void updateEntity(SpecialistSchedule schedule, SpecialistScheduleUpdateDto dto) {
        if (schedule == null || dto == null) {
            return;
        }

        if (dto.getWorkStart() != null) {
            schedule.setWorkStart(dto.getWorkStart());
        }

        if (dto.getWorkEnd() != null) {
            schedule.setWorkEnd(dto.getWorkEnd());
        }

        if (dto.getBreakStart() != null) {
            schedule.setBreakStart(dto.getBreakStart());
        }

        if (dto.getBreakEnd() != null) {
            schedule.setBreakEnd(dto.getBreakEnd());
        }

        if (dto.getSlotDurationMinutes() != null) {
            schedule.setSlotDurationMinutes(dto.getSlotDurationMinutes());
        }

    }
}
