package com.example.pawpalapp.specialistservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Veterinarian {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="vet_seq_gen")
    @SequenceGenerator(allocationSize=1, schema="public",  name="vet_seq_gen", sequenceName = "vetSequence")
    private Long vetId;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;

    private String licenseNumber;

    private String clinicName;

    private int experienceYears;

}
