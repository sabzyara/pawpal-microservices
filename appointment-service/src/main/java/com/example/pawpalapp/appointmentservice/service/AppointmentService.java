package com.example.pawpalapp.appointmentservice.service;


import com.example.pawpalapp.appointmentservice.dto.AppointmentCreateDto;
import com.example.pawpalapp.appointmentservice.dto.AppointmentResponseDto;
import com.example.pawpalapp.appointmentservice.dto.AppointmentUpdateDto;
import com.example.pawpalapp.appointmentservice.model.Appointment;
import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import com.example.pawpalapp.appointmentservice.repository.AppointmentRepository;
import com.example.pawpalapp.appointmentservice.repository.TimeSlotRepository;
import com.example.pawpalapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentCreateDto request) {
        Long petOwnerId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!"OWNER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only pet owners can create appointments");
        }

        LocalDateTime slotDateTime = LocalDateTime.of(request.getDate(), request.getStartTime());
        if (slotDateTime.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot book appointment in the past");
        }

        TimeSlot slot = timeSlotRepository
                .findForUpdate(
                        request.getSpecialistId(),
                        request.getSpecialistType(),
                        request.getDate(),
                        request.getStartTime()
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time slot not found"));

        if (!slot.getUserId().equals(request.getSpecialistId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Slot does not belong to this specialist");
        }

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Time slot is not available");
        }

        if (appointmentRepository.existsByUserIdAndDateAndStartTime(
                request.getSpecialistId(), request.getDate(), request.getStartTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment already exists for this time");
        }

        Appointment appointment = Appointment.builder()
                .userId(request.getSpecialistId())
                .specialistType(request.getSpecialistType())
                .petOwnerId(petOwnerId)
                .petId(request.getPetId())
                .date(request.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(AppointmentStatus.CREATED)
                .ownerNotes(request.getOwnerNotes())
                .build();

        appointment = appointmentRepository.save(appointment);

        log.info("Appointment created: id={}, waiting for confirmation", appointment.getId());

        return toResponseDto(appointment);
    }

    @Transactional
    public AppointmentResponseDto confirmAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (!appointment.getUserId().equals(SecurityUtils.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only specialist can confirm appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only CREATED appointments can be confirmed");
        }

        TimeSlot slot = timeSlotRepository
                .findForUpdate(
                        appointment.getUserId(),
                        appointment.getSpecialistType(),
                        appointment.getDate(),
                        appointment.getStartTime()
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time slot not found"));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Time slot is no longer available");
        }

        slot.setStatus(SlotStatus.BOOKED);
        timeSlotRepository.save(slot);

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        log.info("Confirmed appointment {} and booked slot", id);

        return toResponseDto(appointment);
    }

    public Page<AppointmentResponseDto> getMyAppointments(Pageable pageable, String status) {
        Long petOwnerId = SecurityUtils.getUserId();

        Page<Appointment> appointments;
        if (status != null && !status.isEmpty()) {
            appointments = appointmentRepository.findByPetOwnerIdAndStatus(
                    petOwnerId, AppointmentStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            appointments = appointmentRepository.findByPetOwnerIdOrderByDateDescStartTimeDesc(petOwnerId, pageable);
        }

        return appointments.map(this::toResponseDto);
    }

    public Page<AppointmentResponseDto> getMySpecialistAppointments(Pageable pageable, String status) {
        Long userId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!"VET".equals(role) && !"SERVICE".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only specialists can view appointments");
        }

        Page<Appointment> appointments;
        if (status != null && !status.isEmpty()) {
            appointments = appointmentRepository.findByUserIdAndStatus(
                    userId, AppointmentStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            appointments = appointmentRepository.findByUserIdOrderByDateDescStartTimeDesc(userId, pageable);
        }

        return appointments.map(this::toResponseDto);
    }

    public AppointmentResponseDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String currentRole = SecurityUtils.getRole();

        boolean isOwner = appointment.getPetOwnerId().equals(currentUserId);
        boolean isSpecialist = appointment.getUserId().equals(currentUserId);

        if (!isOwner && !isSpecialist && !"ADMIN".equals(currentRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return toResponseDto(appointment);
    }

    @Transactional
    public AppointmentResponseDto updateAppointment(Long id, AppointmentUpdateDto request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String currentRole = SecurityUtils.getRole();

        boolean isOwner = appointment.getPetOwnerId().equals(currentUserId);
        boolean isSpecialist = appointment.getUserId().equals(currentUserId);

        if (!isOwner && !isSpecialist) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify completed appointment");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify cancelled appointment");
        }

        if (request.getOwnerNotes() != null && isOwner) {
            appointment.setOwnerNotes(request.getOwnerNotes());
        }
        if (request.getSpecialistNotes() != null && isSpecialist) {
            appointment.setSpecialistNotes(request.getSpecialistNotes());
        }

        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        log.info("Updated appointment {} by user {}", id, currentUserId);

        return toResponseDto(appointment);
    }

    @Transactional
    public void cancelAppointment(Long id, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();

        boolean isOwner = appointment.getPetOwnerId().equals(currentUserId);
        boolean isSpecialist = appointment.getUserId().equals(currentUserId);

        if (!isOwner && !isSpecialist) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel completed appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            TimeSlot slot = timeSlotRepository
                    .findByUserIdAndSpecialistTypeAndDateAndStartTime(
                            appointment.getUserId(),
                            appointment.getSpecialistType(),
                            appointment.getDate(),
                            appointment.getStartTime()
                    )
                    .orElse(null);
            if (slot != null && slot.getStatus() == SlotStatus.BOOKED) {
                slot.setStatus(SlotStatus.AVAILABLE);
                timeSlotRepository.save(slot);
            }
        }

        log.info("Cancelled appointment {} with reason: {}", id, reason);
    }

    @Transactional
    public void completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (!appointment.getUserId().equals(SecurityUtils.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only specialist can complete appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only CONFIRMED appointments can be completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        log.info("Completed appointment {}", id);
    }

    public Page<AppointmentResponseDto> getUpcomingAppointments(String userType, Pageable pageable) {
        Long userId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Appointment> appointments;
        if ("owner".equalsIgnoreCase(userType) && "OWNER".equals(role)) {
            appointments = appointmentRepository.findUpcomingByPetOwnerId(userId, today, now);
        } else if ("specialist".equalsIgnoreCase(userType) && ("VET".equals(role) || "SERVICE".equals(role))) {
            appointments = appointmentRepository.findUpcomingByUserId(userId, today, now);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), appointments.size());

        List<AppointmentResponseDto> pagedList = appointments.subList(start, end)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(pagedList, pageable, appointments.size());
    }

    private AppointmentResponseDto toResponseDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .specialistId(appointment.getUserId())
                .specialistType(appointment.getSpecialistType())
                .petOwnerId(appointment.getPetOwnerId())
                .petId(appointment.getPetId())
                .date(appointment.getDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .ownerNotes(appointment.getOwnerNotes())
                .specialistNotes(appointment.getSpecialistNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}