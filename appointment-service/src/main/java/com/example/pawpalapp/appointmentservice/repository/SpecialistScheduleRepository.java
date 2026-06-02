package com.example.pawpalapp.appointmentservice.repository;

import com.example.pawpalapp.appointmentservice.model.SpecialistSchedule;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;


@Repository
public interface SpecialistScheduleRepository extends JpaRepository<SpecialistSchedule, Long> {

    Optional<SpecialistSchedule> findBySpecialistIdAndDayOfWeek(Long specialistId, DayOfWeek dayOfWeek);

    List<SpecialistSchedule> findBySpecialistIdAndSpecialistType(Long specialistId, SpecialistType specialistType);

    Optional<SpecialistSchedule> findBySpecialistIdAndSpecialistTypeAndDayOfWeek(
            Long specialistId, SpecialistType specialistType, DayOfWeek dayOfWeek);

    boolean existsBySpecialistIdAndSpecialistTypeAndDayOfWeek(
            Long specialistId, SpecialistType specialistType, DayOfWeek dayOfWeek);

    @Modifying
    void deleteBySpecialistIdAndSpecialistTypeAndDayOfWeek(
            Long specialistId, SpecialistType specialistType, DayOfWeek dayOfWeek);

    List<SpecialistSchedule> findBySpecialistId(Long specialistId);

}