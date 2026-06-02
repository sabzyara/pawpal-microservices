package com.example.pawpalapp.appointmentservice.model;

import com.example.pawpalapp.appointmentservice.model.enums.AppointmentStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "appointment",
        indexes = {
                @Index(columnList = "specialistId"),
                @Index(columnList = "petOwnerId"),
                @Index(columnList = "timeSlotId"),
                @Index(columnList = "date"),
                @Index(columnList = "status"),
                @Index(columnList = "specialistId, date, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="appointment_seq_gen")
    @SequenceGenerator(allocationSize=1, schema="public",  name="appointment_seq_gen", sequenceName = "appointmentSequence")
    private Long id;

    @Column(nullable = false)
    private Long specialistId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecialistType specialistType;

    @Column(nullable = false)
    private Long petOwnerId;

    @Column(nullable = false)
    private Long petId;

    @Column(nullable = false)
    private Long timeSlotId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    private String cancellationReason;

    @Column(length = 3000)
    private String ownerNotes;

    @Column(length = 3000)
    private String specialistNotes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = AppointmentStatus.CREATED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}