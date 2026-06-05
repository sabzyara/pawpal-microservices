package com.example.pawpalapp.appointmentservice.model;

import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "specialist_schedule",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"specialistId", "specialistType", "dayOfWeek"}
        ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialistSchedule {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="schedule_seq_gen")
    @SequenceGenerator(allocationSize=1, schema="public",  name="schedule_seq_gen", sequenceName = "scheduleSequence")
    private Long id;

    @Column(nullable = false)
    private Long specialistId;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecialistType specialistType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime workStart;

    @Column(nullable = false)
    private LocalTime workEnd;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    @Column(nullable = false)
    private Integer slotDurationMinutes;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (workStart == null || workEnd == null) {
            throw new IllegalArgumentException("Work start and end times are required");
        }

        if (workStart.isAfter(workEnd)) {
            throw new IllegalArgumentException("Work start must be before work end");
        }

        if (breakStart != null && breakEnd != null) {
            if (breakStart.isAfter(breakEnd)) {
                throw new IllegalArgumentException("Break start must be before break end");
            }
            if (breakStart.isBefore(workStart) || breakEnd.isAfter(workEnd)) {
                throw new IllegalArgumentException("Break must be within working hours");
            }
        }

        if (slotDurationMinutes == null || slotDurationMinutes <= 0) {
            throw new IllegalArgumentException("Slot duration must be positive");
        }
    }
}