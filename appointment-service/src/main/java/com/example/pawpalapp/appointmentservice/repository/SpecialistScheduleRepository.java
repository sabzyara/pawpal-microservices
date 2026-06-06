package com.example.pawpalapp.appointmentservice.repository;


import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialistScheduleRepository extends JpaRepository<SpecialistSchedule, Long> {

    Optional<SpecialistSchedule> findBySpecialistIdAndDayOfWeek(Long specialistId, DayOfWeek dayOfWeek);

    List<SpecialistSchedule> findBySpecialistIdAndSpecialistType(Long specialistId, SpecialistType specialistType);

    Optional<SpecialistSchedule> findBySpecialistIdAndSpecialistTypeAndDayOfWeek(
            Long specialistId, SpecialistType specialistType, DayOfWeek dayOfWeek);

    boolean existsBySpecialistIdAndSpecialistTypeAndDayOfWeek(
            Long specialistId, SpecialistType specialistType, DayOfWeek dayOfWeek);

    @Modifying
    @Transactional
    void deleteBySpecialistIdAndSpecialistTypeAndDayOfWeek(
            Long specialistId, SpecialistType specialistType, DayOfWeek dayOfWeek);

    List<SpecialistSchedule> findBySpecialistId(Long specialistId);



}