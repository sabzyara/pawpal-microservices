package com.example.pawpalapp.petmanagementservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pet_seq_gen"
    )
    @SequenceGenerator(
            name = "pet_seq_gen",
            sequenceName = "pet_sequence",
            allocationSize = 1
    )
    private Long id;


    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String name;

    private String species;

    private String breed;

    private String gender;

    private int age;

    private int weight;

    private String healthStatus;
}
