package com.example.pawpalapp.appointmentservice.repository;

import com.example.pawpalapp.appointmentservice.model.TimeSlot;
import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findBySpecialistIdAndDate(Long specialistId, LocalDate date);

    boolean existsBySpecialistIdAndDate(Long specialistId, LocalDate date);

    boolean existsBySpecialistIdAndDateAndStatus(Long specialistId, LocalDate date, SlotStatus status);

    @Modifying
    @Transactional
    @Query("DELETE FROM TimeSlot t WHERE t.specialistId = :specialistId AND t.date = :date AND t.status = :status")
    int deleteBySpecialistIdAndDateAndStatus(@Param("specialistId") Long specialistId,
                                             @Param("date") LocalDate date,
                                             @Param("status") SlotStatus status);


    @Modifying
    @Transactional
    @Query("DELETE FROM TimeSlot t WHERE t.date < :date AND t.status = :status")
    int deleteByDateBeforeAndStatus(@Param("date") LocalDate date, @Param("status") SlotStatus status);

}