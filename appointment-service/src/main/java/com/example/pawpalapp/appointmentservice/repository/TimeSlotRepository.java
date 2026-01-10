package com.example.pawpalapp.appointmentservice.repository;

import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    boolean existsByUserIdAndSpecialistTypeAndDate(
            Long specialistId,
            SpecialistType specialistType,
            LocalDate date
    );

    List<TimeSlot> findByUserIdAndSpecialistTypeAndDate(
            Long specialistId,
            SpecialistType specialistType,
            LocalDate date
    );

    Optional<TimeSlot> findByUserIdAndSpecialistTypeAndDateAndStartTime(
            Long userId,
            SpecialistType specialistType,
            LocalDate date,
            LocalTime startTime
    );


}
