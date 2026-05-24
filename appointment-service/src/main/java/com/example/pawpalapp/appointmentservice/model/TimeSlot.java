package com.example.pawpalapp.appointmentservice.model;

import com.example.pawpalapp.appointmentservice.model.enums.SlotStatus;
import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "time_slots",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "userId",
                                "specialistType",
                                "date",
                                "startTime"
                        }
                )
        },
        indexes = {
                @Index(columnList = "userId"),
                @Index(columnList = "date"),
                @Index(columnList = "status"),
                @Index(columnList = "userId, date, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="time_slot_seq_gen")
    @SequenceGenerator(allocationSize=1, schema="public",  name="time_slot_seq_gen", sequenceName = "timeSlotSequence")
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecialistType specialistType;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status;

    private String blockedReason;

    @CreationTimestamp
    private LocalDateTime createdAt;
}