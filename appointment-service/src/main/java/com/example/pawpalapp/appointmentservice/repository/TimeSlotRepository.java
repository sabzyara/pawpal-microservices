package com.example.pawpalapp.appointmentservice.repository;

import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

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

}
