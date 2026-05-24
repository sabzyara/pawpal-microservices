package com.example.pawpalapp.specialistservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "review_seq_gen"
    )
    @SequenceGenerator(
            allocationSize = 1,
            schema = "public",
            name = "review_seq_gen",
            sequenceName = "reviewSequence"
    )
    private Long reviewId;

    // OWNER
    private Long userId;

    private String userFirstName;

    private String userLastName;

    private String userAvatarUrl;

    // SPECIALIST
    private Long specialistId;

    @Enumerated(EnumType.STRING)
    private SpecialistType specialistType;

    // REVIEW
    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    private LocalDateTime createdAt;
}