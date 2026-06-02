package com.example.pawpalapp.appointmentservice.mapper;

import com.example.pawpalapp.appointmentservice.dto.*;
import com.example.pawpalapp.appointmentservice.model.Appointment;
import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AppointmentMapper {

    private static final String RECOMMENDATIONS_START_MARKER = "=== Рекомендации ===";
    private static final String RECOMMENDATIONS_END_MARKER = "========================";

    public Appointment toEntity(AppointmentCreateDto dto, TimeSlot timeSlot) {
        if (dto == null || timeSlot == null) {
            throw new IllegalArgumentException("AppointmentCreateDto and TimeSlot must not be null");
        }

        return Appointment.builder()
                .specialistId(dto.getSpecialistId())
                .specialistType(dto.getSpecialistType())
                .petOwnerId(dto.getPetOwnerId())
                .petId(dto.getPetId())
                .timeSlotId(timeSlot.getId())
                .date(timeSlot.getDate())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .status(AppointmentStatus.CREATED)
                .ownerNotes(dto.getOwnerNotes())
                .build();
    }

    public AppointmentResponseDto toResponseDto(Appointment appointment, boolean isOwner) {
        if (appointment == null) {
            return null;
        }

        AppointmentResponseDto.AppointmentResponseDtoBuilder builder = AppointmentResponseDto.builder()
                .id(appointment.getId())
                .specialistId(appointment.getSpecialistId())
                .specialistType(appointment.getSpecialistType())
                .petOwnerId(appointment.getPetOwnerId())
                .petId(appointment.getPetId())
                .timeSlotId(appointment.getTimeSlotId())
                .date(appointment.getDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .cancellationReason(appointment.getCancellationReason())
                .ownerNotes(appointment.getOwnerNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt());

        if (isOwner) {
            builder.specialistNotes(extractRecommendations(appointment.getSpecialistNotes()));
        } else {
            builder.specialistNotes(appointment.getSpecialistNotes());
        }

        return builder.build();
    }

    public AppointmentResponseDto toResponseDto(Appointment appointment) {
        return toResponseDto(appointment, false);
    }

    public void updateEntity(Appointment appointment, AppointmentUpdateDto dto) {
        if (appointment == null || dto == null) {
            return;
        }

        if (dto.getOwnerNotes() != null) {
            appointment.setOwnerNotes(dto.getOwnerNotes());
        }

        if (dto.getSpecialistNotes() != null) {
            appointment.setSpecialistNotes(dto.getSpecialistNotes());
        }

        if (dto.getStatus() != null) {
            appointment.setStatus(dto.getStatus());
        }
    }

    public void addRecommendations(Appointment appointment, AppointmentRecommendationsDto dto) {
        if (appointment == null || dto == null || dto.getRecommendations() == null) {
            return;
        }

        String currentNotes = appointment.getSpecialistNotes();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        String newRecommendations = String.format(
                "\n\n%s [%s]\n%s\n%s\n",
                RECOMMENDATIONS_START_MARKER,
                timestamp,
                dto.getRecommendations(),
                RECOMMENDATIONS_END_MARKER
        );

        if (currentNotes != null && !currentNotes.isEmpty()) {
            appointment.setSpecialistNotes(currentNotes + newRecommendations);
        } else {
            appointment.setSpecialistNotes(newRecommendations);
        }
    }

    public String extractRecommendations(String specialistNotes) {
        if (specialistNotes == null || specialistNotes.isEmpty()) {
            return null;
        }

        StringBuilder recommendations = new StringBuilder();
        int startIndex = 0;

        while (true) {
            int markerStart = specialistNotes.indexOf(RECOMMENDATIONS_START_MARKER, startIndex);
            if (markerStart == -1) break;

            int markerEnd = specialistNotes.indexOf(RECOMMENDATIONS_END_MARKER, markerStart);
            if (markerEnd == -1) {
                recommendations.append(specialistNotes.substring(markerStart));
                break;
            } else {
                recommendations.append(specialistNotes.substring(markerStart, markerEnd + RECOMMENDATIONS_END_MARKER.length()));
                startIndex = markerEnd + RECOMMENDATIONS_END_MARKER.length();
            }
        }

        return recommendations.length() > 0 ? recommendations.toString() : null;
    }



    public Appointment prepareReschedule(Appointment oldAppointment,
                                         AppointmentRescheduleDto dto,
                                         TimeSlot newTimeSlot) {
        if (oldAppointment == null || dto == null || newTimeSlot == null) {
            return null;
        }

        return Appointment.builder()
                .specialistId(newTimeSlot.getSpecialistId())
                .specialistType(newTimeSlot.getSpecialistType())
                .timeSlotId(newTimeSlot.getId())
                .petOwnerId(oldAppointment.getPetOwnerId())
                .petId(oldAppointment.getPetId())
                .date(newTimeSlot.getDate())
                .startTime(newTimeSlot.getStartTime())
                .endTime(newTimeSlot.getEndTime())
                .status(AppointmentStatus.CREATED)
                .ownerNotes(oldAppointment.getOwnerNotes())
                .specialistNotes(oldAppointment.getSpecialistNotes())
                .build();
    }

}