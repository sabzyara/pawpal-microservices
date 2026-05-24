package com.example.pawpalapp.appointmentservice.repository;

import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialistScheduleRepository extends JpaRepository<SpecialistSchedule, Long> {

    Optional<SpecialistSchedule> findByUserIdAndSpecialistTypeAndDayOfWeek(
            Long userId, SpecialistType specialistType, DayOfWeek dayOfWeek);

    List<SpecialistSchedule> findByUserIdAndSpecialistType(Long userId, SpecialistType specialistType);

    void deleteByUserIdAndSpecialistTypeAndDayOfWeek(Long userId, SpecialistType specialistType, DayOfWeek dayOfWeek);
}