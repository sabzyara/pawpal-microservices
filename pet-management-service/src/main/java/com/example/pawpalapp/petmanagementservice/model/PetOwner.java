package com.example.pawpalapp.petmanagementservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pet_owner_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetOwner {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pet_owner_seq_gen"
    )
    @SequenceGenerator(
            name = "pet_owner_seq_gen",
            sequenceName = "pet_owner_sequence",
            allocationSize = 1
    )
    private Long id;


    @Column(nullable = false, unique = true)
    private Long userId;

    private String username;

    private String phoneNumber;

    private String address;


}
