package com.example.pawpalapp.appointmentservice.service;

import com.example.pawpalapp.appointmentservice.dto.*;
import com.example.pawpalapp.appointmentservice.mapper.AppointmentMapper;
import com.example.pawpalapp.appointmentservice.model.Appointment;
import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import com.example.pawpalapp.appointmentservice.repository.AppointmentRepository;
import com.example.pawpalapp.appointmentservice.repository.TimeSlotRepository;
import com.example.pawpalapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentCreateDto request) {
        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!"OWNER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only pet owners can create appointments");
        }

        if (!request.getPetOwnerId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create appointments for your own pets");
        }

        TimeSlot slot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time slot not found"));

        if (!slot.getSpecialistId().equals(request.getSpecialistId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot does not belong to this specialist");
        }

        if (slot.getSpecialistType() != request.getSpecialistType()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specialist type mismatch");
        }

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Time slot is not available");
        }

        LocalDateTime slotDateTime = LocalDateTime.of(slot.getDate(), slot.getStartTime());
        if (slotDateTime.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot book appointment in the past");
        }

        if (appointmentRepository.existsByTimeSlotId(slot.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment already exists for this time slot");
        }

        Appointment appointment = appointmentMapper.toEntity(request, slot);
        appointment = appointmentRepository.save(appointment);

        slot.book(currentUserId);
        timeSlotRepository.save(slot);

        log.info("Appointment created: id={}, slotId={}, petId={}, specialistId={}",
                appointment.getId(), slot.getId(), appointment.getPetId(), appointment.getSpecialistId());

        return appointmentMapper.toResponseDto(appointment);
    }

    @Transactional
    public AppointmentResponseDto confirmAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!appointment.getSpecialistId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only specialist can confirm appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Only CREATED appointments can be confirmed. Current status: %s", appointment.getStatus()));
        }

        TimeSlot slot = timeSlotRepository.findById(appointment.getTimeSlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time slot not found"));

        if (slot.getStatus() != SlotStatus.BOOKED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Time slot is no longer booked");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        log.info("Confirmed appointment {} by specialist {}", id, currentUserId);

        return appointmentMapper.toResponseDto(appointment);
    }

    public AppointmentResponseDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String currentRole = SecurityUtils.getRole();

        boolean isOwner = appointment.getPetOwnerId().equals(currentUserId);
        boolean isSpecialist = appointment.getSpecialistId().equals(currentUserId);

        if (!isOwner && !isSpecialist && !"ADMIN".equals(currentRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        boolean showOnlyRecommendations = isOwner && !isSpecialist;

        return appointmentMapper.toResponseDto(appointment, showOnlyRecommendations);
    }

    public Page<AppointmentResponseDto> getMyAppointments(Pageable pageable, String status) {
        Long petOwnerId = SecurityUtils.getUserId();

        Page<Appointment> appointments;
        if (status != null && !status.isEmpty()) {
            try {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
                appointments = appointmentRepository.findByPetOwnerIdAndStatus(petOwnerId, appointmentStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
            }
        } else {
            appointments = appointmentRepository.findByPetOwnerIdOrderByDateDescStartTimeDesc(petOwnerId, pageable);
        }

        return appointments.map(app -> appointmentMapper.toResponseDto(app, true));
    }

    public Page<AppointmentResponseDto> getMySpecialistAppointments(Pageable pageable, String status) {
        Long specialistId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!isSpecialistRole(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only specialists can view appointments");
        }

        Page<Appointment> appointments;
        if (status != null && !status.isEmpty()) {
            try {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
                appointments = appointmentRepository.findBySpecialistIdAndStatus(specialistId, appointmentStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
            }
        } else {
            appointments = appointmentRepository.findBySpecialistIdOrderByDateDescStartTimeDesc(specialistId, pageable);
        }

        return appointments.map(appointmentMapper::toResponseDto);
    }

    @Transactional
    public AppointmentResponseDto updateAppointment(Long id, AppointmentUpdateDto request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String currentRole = SecurityUtils.getRole();

        boolean isOwner = appointment.getPetOwnerId().equals(currentUserId);
        boolean isSpecialist = appointment.getSpecialistId().equals(currentUserId);
        boolean isAdmin = "ADMIN".equals(currentRole);

        if (!isOwner && !isSpecialist && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify completed appointment");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED_BY_USER ||
                appointment.getStatus() == AppointmentStatus.CANCELLED_BY_SPECIALIST) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify cancelled appointment");
        }

        if (request.getOwnerNotes() != null && (isOwner || isAdmin)) {
            appointment.setOwnerNotes(request.getOwnerNotes());
        }

        if (request.getSpecialistNotes() != null && (isSpecialist || isAdmin)) {
            appointment.setSpecialistNotes(request.getSpecialistNotes());
        }

        if (request.getStatus() != null && (isSpecialist || isAdmin)) {
            validateStatusTransition(appointment.getStatus(), request.getStatus());
            appointment.setStatus(request.getStatus());
        }

        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        log.info("Updated appointment {} by user {} with role {}", id, currentUserId, currentRole);

        return appointmentMapper.toResponseDto(appointment);
    }

    @Transactional
    public void cancelAppointment(Long id, AppointmentCancelDto cancelDto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String currentRole = SecurityUtils.getRole();

        boolean isOwner = appointment.getPetOwnerId().equals(currentUserId);
        boolean isSpecialist = appointment.getSpecialistId().equals(currentUserId);
        boolean isAdmin = "ADMIN".equals(currentRole);

        if (!isOwner && !isSpecialist && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel completed appointment");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED_BY_USER ||
                appointment.getStatus() == AppointmentStatus.CANCELLED_BY_SPECIALIST) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment already cancelled");
        }

        if (isOwner) {
            appointment.setStatus(AppointmentStatus.CANCELLED_BY_USER);
        } else if (isSpecialist) {
            appointment.setStatus(AppointmentStatus.CANCELLED_BY_SPECIALIST);
        } else {
            appointment.setStatus(AppointmentStatus.CANCELLED_BY_USER);
        }

        appointment.setCancellationReason(cancelDto.getCancellationReason());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        TimeSlot slot = timeSlotRepository.findById(appointment.getTimeSlotId()).orElse(null);
        if (slot != null && slot.getStatus() == SlotStatus.BOOKED) {
            slot.release();
            timeSlotRepository.save(slot);
            log.info("Released slot {} for cancelled appointment {}", slot.getId(), id);
        }

        log.info("Cancelled appointment {} by user {} with reason: {}", id, currentUserId, cancelDto.getCancellationReason());
    }

    @Transactional
    public AppointmentResponseDto rescheduleAppointment(AppointmentRescheduleDto request) {
        Appointment oldAppointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        boolean isOwner = oldAppointment.getPetOwnerId().equals(currentUserId);
        boolean isSpecialist = oldAppointment.getSpecialistId().equals(currentUserId);

        if (!isOwner && !isSpecialist && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (oldAppointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reschedule completed appointment");
        }
        if (oldAppointment.getStatus() == AppointmentStatus.CANCELLED_BY_USER ||
                oldAppointment.getStatus() == AppointmentStatus.CANCELLED_BY_SPECIALIST) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reschedule cancelled appointment");
        }

        TimeSlot newSlot = timeSlotRepository.findById(request.getNewTimeSlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "New time slot not found"));

        if (newSlot.getStatus() != SlotStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New time slot is not available");
        }

        if (!newSlot.getSpecialistId().equals(oldAppointment.getSpecialistId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reschedule to another specialist");
        }

        LocalDateTime slotDateTime = LocalDateTime.of(newSlot.getDate(), newSlot.getStartTime());
        if (slotDateTime.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reschedule to past time slot");
        }

        if (appointmentRepository.existsByTimeSlotId(newSlot.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New time slot already booked");
        }

        if (isOwner) {
            oldAppointment.setStatus(AppointmentStatus.CANCELLED_BY_USER);
        } else {
            oldAppointment.setStatus(AppointmentStatus.CANCELLED_BY_SPECIALIST);
        }
        oldAppointment.setCancellationReason("Rescheduled to slot " + newSlot.getId() +
                " (Reason: " + (request.getReason() != null ? request.getReason() : "No reason provided") + ")");
        oldAppointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(oldAppointment);

        TimeSlot oldSlot = timeSlotRepository.findById(oldAppointment.getTimeSlotId()).orElse(null);
        if (oldSlot != null && oldSlot.getStatus() == SlotStatus.BOOKED) {
            oldSlot.release();
            timeSlotRepository.save(oldSlot);
        }

        Appointment newAppointment = appointmentMapper.prepareReschedule(oldAppointment, request, newSlot);
        newAppointment = appointmentRepository.save(newAppointment);

        newSlot.book(currentUserId);
        timeSlotRepository.save(newSlot);

        log.info("Rescheduled appointment {} to new slot {} by user {}",
                request.getAppointmentId(), newSlot.getId(), currentUserId);

        return appointmentMapper.toResponseDto(newAppointment);
    }

    @Transactional
    public AppointmentResponseDto addRecommendations(Long id, AppointmentRecommendationsDto dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!appointment.getSpecialistId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only specialist can add recommendations");
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Can only add recommendations after appointment is completed. Current status: %s",
                            appointment.getStatus()));
        }

        if (dto.getRecommendations() == null || dto.getRecommendations().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recommendations cannot be empty");
        }

        appointmentMapper.addRecommendations(appointment, dto);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        log.info("Added recommendations to appointment {} by specialist {}", id, currentUserId);

        return appointmentMapper.toResponseDto(appointment);
    }

    public String getRecommendationsForOwner(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();

        if (!appointment.getPetOwnerId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is not completed yet");
        }

        String recommendations = appointmentMapper.extractRecommendations(appointment.getSpecialistNotes());

        if (recommendations == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No recommendations found for this appointment");
        }

        return recommendations;
    }

    @Transactional
    public void completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!appointment.getSpecialistId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only specialist can complete appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Only CONFIRMED appointments can be completed. Current status: %s", appointment.getStatus()));
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        log.info("Completed appointment {} by specialist {}", id, currentUserId);
    }

    public Page<AppointmentResponseDto> getUpcomingAppointments(String userType, Pageable pageable) {
        Long userId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<AppointmentStatus> statuses = Arrays.asList(AppointmentStatus.CREATED, AppointmentStatus.CONFIRMED);
        List<Appointment> appointments;

        if ("owner".equalsIgnoreCase(userType)) {
            if (!"OWNER".equals(role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            appointments = appointmentRepository.findUpcomingByPetOwnerId(userId, today, now, statuses);
            return createPageResponse(appointments, pageable, true);

        } else if ("specialist".equalsIgnoreCase(userType)) {
            if (!isSpecialistRole(role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            appointments = appointmentRepository.findUpcomingBySpecialistId(userId, today, now, statuses);
            return createPageResponse(appointments, pageable, false);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userType. Use 'owner' or 'specialist'");
        }
    }

    private Page<AppointmentResponseDto> createPageResponse(List<Appointment> appointments, Pageable pageable, boolean showOnlyRecommendations) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), appointments.size());

        if (start >= appointments.size()) {
            return new PageImpl<>(List.of(), pageable, appointments.size());
        }

        List<AppointmentResponseDto> pagedList = appointments.subList(start, end)
                .stream()
                .map(app -> {
                    if (showOnlyRecommendations) {
                        return appointmentMapper.toResponseDto(app, true);
                    } else {
                        return appointmentMapper.toResponseDto(app);
                    }
                })
                .collect(Collectors.toList());

        return new PageImpl<>(pagedList, pageable, appointments.size());
    }

    @Transactional
    public AppointmentResponseDto markAsNoShow(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!appointment.getSpecialistId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only specialist can mark appointment as no-show");
        }

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Only CONFIRMED appointments can be marked as no-show. Current status: %s", appointment.getStatus()));
        }

        LocalDateTime appointmentEndTime = LocalDateTime.of(appointment.getDate(), appointment.getEndTime());
        if (appointmentEndTime.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot mark as no-show before appointment ends");
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointment.setUpdatedAt(LocalDateTime.now());

        String currentNotes = appointment.getSpecialistNotes();
        String noShowNote = "[NO-SHOW] Client did not attend appointment on " + LocalDate.now();
        appointment.setSpecialistNotes(currentNotes == null ? noShowNote : currentNotes + "\n" + noShowNote);

        appointment = appointmentRepository.save(appointment);

        log.info("Marked appointment {} as NO_SHOW by specialist {}", id, currentUserId);

        return appointmentMapper.toResponseDto(appointment);
    }

    private boolean isSpecialistRole(String role) {
        return "VET".equals(role) || "SERVICE".equals(role);
    }

    private void validateStatusTransition(AppointmentStatus currentStatus, AppointmentStatus newStatus) {
        switch (currentStatus) {
            case CREATED:
                if (newStatus != AppointmentStatus.CONFIRMED &&
                        newStatus != AppointmentStatus.CANCELLED_BY_USER &&
                        newStatus != AppointmentStatus.CANCELLED_BY_SPECIALIST) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid status transition from CREATED to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != AppointmentStatus.COMPLETED &&
                        newStatus != AppointmentStatus.CANCELLED_BY_USER &&
                        newStatus != AppointmentStatus.CANCELLED_BY_SPECIALIST &&
                        newStatus != AppointmentStatus.NO_SHOW) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid status transition from CONFIRMED to " + newStatus);
                }
                break;
            case COMPLETED:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change status from COMPLETED");
            case CANCELLED_BY_USER:
            case CANCELLED_BY_SPECIALIST:
            case NO_SHOW:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change status from " + currentStatus);
            default:
                break;
        }
    }
}