package com.example.pawpalapp.appointmentservice.repository;

import com.example.pawpalapp.appointmentservice.model.Appointment;
import com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByTimeSlotId(Long timeSlotId);



    @Query("SELECT a FROM Appointment a WHERE a.id = :id")


    Page<Appointment> findByPetOwnerIdOrderByDateDescStartTimeDesc(Long petOwnerId, Pageable pageable);


    Page<Appointment> findByPetOwnerIdAndStatus(Long petOwnerId, AppointmentStatus status, Pageable pageable);



    @Query("SELECT a FROM Appointment a " +
            "WHERE a.petOwnerId = :petOwnerId " +
            "AND (a.date > :today OR (a.date = :today AND a.startTime >= :now)) " +
            "AND a.status IN (" +
            "T(com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus).CREATED, " +
            "T(com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus).CONFIRMED) " +
            "ORDER BY a.date ASC, a.startTime ASC")
    List<Appointment> findUpcomingByPetOwnerId(@Param("petOwnerId") Long petOwnerId,
                                               @Param("today") LocalDate today,
                                               @Param("now") LocalTime now);


    @Query("SELECT a FROM Appointment a " +
            "WHERE a.petOwnerId = :petOwnerId " +
            "AND (a.date < :today OR (a.date = :today AND a.startTime < :now)) " +
            "ORDER BY a.date DESC, a.startTime DESC")

    Page<Appointment> findBySpecialistIdOrderByDateDescStartTimeDesc(Long specialistId, Pageable pageable);


    Page<Appointment> findBySpecialistIdAndStatus(Long specialistId, AppointmentStatus status, Pageable pageable);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.specialistId = :specialistId " +
            "AND (a.date > :today OR (a.date = :today AND a.startTime >= :now)) " +
            "AND a.status IN (" +
            "T(com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus).CREATED, " +
            "T(com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus).CONFIRMED) " +
            "ORDER BY a.date ASC, a.startTime ASC")
    List<Appointment> findUpcomingBySpecialistId(@Param("specialistId") Long specialistId,
                                                 @Param("today") LocalDate today,
                                                 @Param("now") LocalTime now);

}