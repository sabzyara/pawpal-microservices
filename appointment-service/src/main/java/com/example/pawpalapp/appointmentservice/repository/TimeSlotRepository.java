package com.example.pawpalapp.appointmentservice.repository;

import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    // PESSIMISTIC WRITE LOCK для предотвращения race condition
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT t FROM TimeSlot t
        WHERE t.userId = :userId
        AND t.specialistType = :specialistType
        AND t.date = :date
        AND t.startTime = :startTime
    """)
    Optional<TimeSlot> findForUpdate(
            @Param("userId") Long userId,
            @Param("specialistType") SpecialistType specialistType,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime
    );

    Optional<TimeSlot> findByUserIdAndSpecialistTypeAndDateAndStartTime(
            Long userId, SpecialistType specialistType, LocalDate date, LocalTime startTime);

    List<TimeSlot> findByUserIdAndSpecialistTypeAndDateOrderByStartTimeAsc(
            Long userId, SpecialistType specialistType, LocalDate date);

    Page<TimeSlot> findByUserIdAndSpecialistTypeAndDateAndStatus(
            Long userId, SpecialistType specialistType, LocalDate date, SlotStatus status, Pageable pageable);

    boolean existsByUserIdAndSpecialistTypeAndDate(Long userId, SpecialistType specialistType, LocalDate date);

    void deleteByUserIdAndSpecialistTypeAndDateAndStatus(
            Long userId, SpecialistType specialistType, LocalDate date, SlotStatus status);
}