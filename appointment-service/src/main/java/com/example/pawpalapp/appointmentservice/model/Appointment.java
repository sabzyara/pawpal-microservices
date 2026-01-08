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
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                        "userId",
                        "specialistType",
                        "date",
                        "startTime"
                }
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private SpecialistType specialistType;

    private Long petOwnerId;

    private Long petId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private LocalDateTime createdAt;
}

