package com.example.pawpalapp.appointmentservice.service;

import com.example.pawpalapp.appointmentservice.dto.TimeSlotAvailableRequestDto;
import com.example.pawpalapp.appointmentservice.dto.TimeSlotResponseDto;
import com.example.pawpalapp.appointmentservice.mapper.TimeSlotMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlotGenerationService {

    private final TimeSlotRepository timeSlotRepository;
    private final SpecialistScheduleRepository scheduleRepository;
    private final TimeSlotMapper timeSlotMapper;

    private static final int DAYS_TO_GENERATE = 30;

    private final ConcurrentMap<String, Boolean> generationLocks = new ConcurrentHashMap<>();

    @Transactional
    public void generateSlotsForNewSchedule(SpecialistSchedule schedule) {
        log.info("Generating slots for new schedule: specialist={}, dayOfWeek={}, workStart={}, workEnd={}",
                schedule.getSpecialistId(), schedule.getDayOfWeek(),
                schedule.getWorkStart(), schedule.getWorkEnd());

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(DAYS_TO_GENERATE);

        int generatedCount = generateMissingSlotsForSchedule(schedule, today, endDate);

        log.info("Generated {} slots for specialist {} on {} (next 30 days)",
                generatedCount, schedule.getSpecialistId(), schedule.getDayOfWeek());
    }

    @Transactional
    public void generateSlotsForAllSchedulesOfSpecialist(Long specialistId) {
        log.info("Generating slots for all schedules of specialist: {}", specialistId);

        List<SpecialistSchedule> schedules = scheduleRepository.findBySpecialistId(specialistId);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(DAYS_TO_GENERATE);

        int totalGenerated = 0;
        for (SpecialistSchedule schedule : schedules) {
            totalGenerated += generateMissingSlotsForSchedule(schedule, today, endDate);
        }

        log.info("Generated total {} slots for specialist {}", totalGenerated, specialistId);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void scheduledSlotGeneration() {
        log.info("=== STARTING SCHEDULED SLOT GENERATION ===");
        long startTime = System.currentTimeMillis();

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(DAYS_TO_GENERATE);

        List<SpecialistSchedule> allSchedules = scheduleRepository.findAll();
        int totalGenerated = 0;

        for (SpecialistSchedule schedule : allSchedules) {
            totalGenerated += generateMissingSlotsForSchedule(schedule, today, endDate);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("=== SCHEDULED GENERATION COMPLETED: {} slots generated in {} ms ===",
                totalGenerated, duration);
    }

    @Transactional
    public void regenerateSlotsForSchedule(SpecialistSchedule schedule) {
        log.info("Regenerating slots for schedule: specialist={}, dayOfWeek={}",
                schedule.getSpecialistId(), schedule.getDayOfWeek());

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(DAYS_TO_GENERATE);

        int deletedCount = 0;
        LocalDate currentDate = today;
        List<LocalDate> datesToDelete = new ArrayList<>();

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() == schedule.getDayOfWeek()) {
                boolean hasBookedSlots = timeSlotRepository.existsBySpecialistIdAndDateAndStatus(
                        schedule.getSpecialistId(), currentDate, SlotStatus.BOOKED
                );

                if (!hasBookedSlots) {
                    int deleted = timeSlotRepository.deleteBySpecialistIdAndDateAndStatus(
                            schedule.getSpecialistId(), currentDate, SlotStatus.AVAILABLE
                    );
                    deletedCount += deleted;
                    datesToDelete.add(currentDate);
                } else {
                    log.warn("Skipping regeneration for date {} because has BOOKED slots", currentDate);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        log.info("Deleted {} old available slots on {} dates", deletedCount, datesToDelete.size());

        int generatedCount = 0;
        for (LocalDate date : datesToDelete) {
            List<TimeSlot> newSlots = timeSlotMapper.generateSlotsFromSchedule(schedule, date);
            if (!newSlots.isEmpty()) {
                timeSlotRepository.saveAll(newSlots);
                generatedCount += newSlots.size();
            }
        }

        log.info("Regeneration completed: {} new slots generated", generatedCount);
    }

    @Transactional
    public void regenerateSlotsForDate(Long specialistId, LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot regenerate slots for past dates");
        }

        SpecialistSchedule schedule = scheduleRepository
                .findBySpecialistIdAndDayOfWeek(specialistId, date.getDayOfWeek())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No schedule found for specialist %d on %s",
                                specialistId, date.getDayOfWeek())));

        boolean hasBookedSlots = timeSlotRepository.existsBySpecialistIdAndDateAndStatus(
                specialistId, date, SlotStatus.BOOKED
        );

        if (hasBookedSlots) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot regenerate slots on date with existing bookings");
        }

        timeSlotRepository.deleteBySpecialistIdAndDateAndStatus(specialistId, date, SlotStatus.AVAILABLE);

        List<TimeSlot> newSlots = timeSlotMapper.generateSlotsFromSchedule(schedule, date);

        if (!newSlots.isEmpty()) {
            timeSlotRepository.saveAll(newSlots);
            log.info("Regenerated {} slots for specialist {} on date {}",
                    newSlots.size(), specialistId, date);
        }
    }

    public Page<TimeSlotResponseDto> getAvailableSlots(Long specialistId, SpecialistType specialistType,
                                                       LocalDate date, Pageable pageable) {
        if (date == null) {
            return Page.empty(pageable);
        }

        if (date.isBefore(LocalDate.now())) {
            return Page.empty(pageable);
        }

        List<TimeSlot> slots = getOrGenerateSlots(specialistId, date);

        List<TimeSlot> availableSlots = slots.stream()
                .filter(slot -> slot.getStatus() == SlotStatus.AVAILABLE)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), availableSlots.size());

        if (start >= availableSlots.size()) {
            return Page.empty(pageable);
        }

        List<TimeSlotResponseDto> content = availableSlots.subList(start, end)
                .stream()
                .map(timeSlotMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, availableSlots.size());
    }

    private List<TimeSlot> getOrGenerateSlots(Long specialistId, LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            return new ArrayList<>();
        }

        List<TimeSlot> existingSlots = timeSlotRepository.findBySpecialistIdAndDate(specialistId, date);

        if (!existingSlots.isEmpty()) {
            return existingSlots;
        }

        String lockKey = specialistId + "_" + date;
        if (generationLocks.putIfAbsent(lockKey, true) == null) {
            try {
                return generateSlotsForDateWithLock(specialistId, date);
            } finally {
                generationLocks.remove(lockKey);
            }
        }

        return waitForGenerationCompletion(specialistId, date);
    }

    private List<TimeSlot> generateSlotsForDateWithLock(Long specialistId, LocalDate date) {
        log.info("Lazy generating slots for specialist {} on {}", specialistId, date);

        SpecialistSchedule schedule = scheduleRepository
                .findBySpecialistIdAndDayOfWeek(specialistId, date.getDayOfWeek())
                .orElse(null);

        if (schedule == null) {
            log.warn("No schedule found for specialist {} on {}", specialistId, date.getDayOfWeek());
            return new ArrayList<>();
        }

        List<TimeSlot> newSlots = timeSlotMapper.generateSlotsFromSchedule(schedule, date);

        if (!newSlots.isEmpty()) {
            timeSlotRepository.saveAll(newSlots);
            log.info("Lazy generated {} slots for specialist {} on {}",
                    newSlots.size(), specialistId, date);
        }

        return newSlots;
    }

    private List<TimeSlot> waitForGenerationCompletion(Long specialistId, LocalDate date) {
        for (int retries = 0; retries < 20; retries++) {
            try {
                Thread.sleep(100);
                List<TimeSlot> slots = timeSlotRepository.findBySpecialistIdAndDate(specialistId, date);
                if (!slots.isEmpty()) {
                    return slots;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return new ArrayList<>();
    }

    private int generateMissingSlotsForSchedule(SpecialistSchedule schedule,
                                                LocalDate startDate,
                                                LocalDate endDate) {
        LocalDate actualStartDate = startDate.isBefore(LocalDate.now()) ? LocalDate.now() : startDate;

        List<LocalDate> datesToGenerate = new ArrayList<>();
        LocalDate currentDate = actualStartDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() == schedule.getDayOfWeek()) {
                boolean hasAnySlots = timeSlotRepository.existsBySpecialistIdAndDate(
                        schedule.getSpecialistId(), currentDate
                );

                if (!hasAnySlots) {
                    datesToGenerate.add(currentDate);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        if (datesToGenerate.isEmpty()) {
            return 0;
        }

        List<TimeSlot> allSlots = new ArrayList<>();
        for (LocalDate date : datesToGenerate) {
            List<TimeSlot> slots = timeSlotMapper.generateSlotsFromSchedule(schedule, date);
            allSlots.addAll(slots);
        }

        if (!allSlots.isEmpty()) {
            timeSlotRepository.saveAll(allSlots);
            log.debug("Generated {} slots for specialist {} on {} dates",
                    allSlots.size(), schedule.getSpecialistId(), datesToGenerate.size());
        }

        return allSlots.size();
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOldSlots() {
        LocalDate thresholdDate = LocalDate.now().minusDays(30);
        int deleted = timeSlotRepository.deleteByDateBeforeAndStatus(thresholdDate, SlotStatus.BOOKED);

        if (deleted > 0) {
            log.info("Cleaned up {} old booked slots before {}", deleted, thresholdDate);
        }

        int deletedAvailable = timeSlotRepository.deleteByDateBeforeAndStatus(thresholdDate, SlotStatus.AVAILABLE);
        if (deletedAvailable > 0) {
            log.info("Cleaned up {} old available slots before {}", deletedAvailable, thresholdDate);
        }

        int deletedBlocked = timeSlotRepository.deleteByDateBeforeAndStatus(thresholdDate, SlotStatus.BLOCKED);
        if (deletedBlocked > 0) {
            log.info("Cleaned up {} old blocked slots before {}", deletedBlocked, thresholdDate);
        }
    }

    public Page<TimeSlotResponseDto> getAvailableSlots(TimeSlotAvailableRequestDto request, Pageable pageable) {
        return getAvailableSlots(request.getSpecialistId(), request.getSpecialistType(),
                request.getDate(), pageable);
    }

    public List<TimeSlotResponseDto> getSlotsByDate(Long specialistId, SpecialistType specialistType, LocalDate date) {
        List<TimeSlot> slots = getOrGenerateSlots(specialistId, date);
        return timeSlotMapper.toResponseDtoList(slots);
    }

    public Page<TimeSlotResponseDto> getMyAvailableSlots(LocalDate date, Pageable pageable) {
        Long specialistId = SecurityUtils.getUserId();
        SpecialistType specialistType = resolveSpecialistType(SecurityUtils.getRole());
        return getAvailableSlots(specialistId, specialistType, date, pageable);
    }

    @Transactional
    public TimeSlotResponseDto blockSlot(Long slotId, String reason) {
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time slot not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!slot.getSpecialistId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only block your own slots");
        }

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Cannot block slot with status: %s", slot.getStatus()));
        }

        slot.setStatus(SlotStatus.BLOCKED);
        slot.setBlockedReason(reason);

        TimeSlot saved = timeSlotRepository.save(slot);
        log.info("Blocked slot {} by specialist {}", slotId, currentUserId);

        return timeSlotMapper.toResponseDto(saved);
    }

    @Transactional
    public TimeSlotResponseDto unblockSlot(Long slotId) {
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time slot not found"));

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getRole();

        if (!slot.getSpecialistId().equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only unblock your own slots");
        }

        if (slot.getStatus() != SlotStatus.BLOCKED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Cannot unblock slot with status: %s", slot.getStatus()));
        }

        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setBlockedReason(null);

        TimeSlot saved = timeSlotRepository.save(slot);
        log.info("Unblocked slot {} by specialist {}", slotId, currentUserId);

        return timeSlotMapper.toResponseDto(saved);
    }

    private SpecialistType resolveSpecialistType(String role) {
        String upperRole = role.toUpperCase();
        return switch (upperRole) {
            case "VET" -> SpecialistType.VET;
            case "SERVICE" -> SpecialistType.SERVICE;
            default -> throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Invalid specialist role: " + role
            );
        };
    }
}