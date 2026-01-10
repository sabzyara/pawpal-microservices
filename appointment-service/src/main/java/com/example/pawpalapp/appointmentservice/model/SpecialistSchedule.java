package com.example.pawpalapp.appointmentservice.model;

import com.example.pawpalapp.appointmentservice.model.enums.SpecialistType;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"userId", "specialistType", "dayOfWeek"}
        )
)
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

    private Long userId;

    @Enumerated(EnumType.STRING)
    private SpecialistType specialistType;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime workStart;

    private LocalTime workEnd;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    private Integer slotDurationMinutes;
}

