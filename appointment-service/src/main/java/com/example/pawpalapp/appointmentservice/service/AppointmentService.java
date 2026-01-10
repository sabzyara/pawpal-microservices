package com.example.pawpalapp.appointmentservice.service;

import com.example.pawpalapp.appointmentservice.dto.AppointmentCreateDto;
import com.example.pawpalapp.appointmentservice.model.Appointment;
import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import com.example.pawpalapp.appointmentservice.repository.AppointmentRepository;
import com.example.pawpalapp.appointmentservice.repository.TimeSlotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public Appointment create(AppointmentCreateDto dto) throws AccessDeniedException {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long petOwnerId = jwt.getClaim("userId");
        String role = jwt.getClaim("role");

        if (!"OWNER".equals(role)) {
            throw new AccessDeniedException(
                    "Only pet owners can create appointments");
        }


        // FINDING SLOT
        TimeSlot slot = timeSlotRepository
                .findByUserIdAndSpecialistTypeAndDateAndStartTime(
                        dto.getSpecialistId(),
                        dto.getSpecialistType(),
                        dto.getDate(),
                        dto.getStartTime()
                )
                .orElseThrow(() ->
                        new IllegalStateException("Time slot not found"));

        // CHECKING STATUS OF SLOT
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Slot is not available");
        }

        // CREATE AN APPOINTMENT
        Appointment appointment = Appointment.builder()
                .userId(dto.getSpecialistId())
                .specialistType(dto.getSpecialistType())
                .petOwnerId(petOwnerId)
                .petId(dto.getPetId())
                .date(dto.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(AppointmentStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        appointmentRepository.save(appointment);

        // UPDATE SLOT STATUS
        slot.setStatus(SlotStatus.BOOKED);
        timeSlotRepository.save(slot);

        return appointment;
    }
}
