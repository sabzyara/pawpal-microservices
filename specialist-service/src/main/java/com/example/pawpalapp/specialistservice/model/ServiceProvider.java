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
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="serv_seq_gen")
    @SequenceGenerator(allocationSize=1, name="serv_seq_gen", sequenceName = "servSequence")
    private Long serviceProviderId;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;

    private String serviceCategory;

    private Integer experienceYears;

    private String education;

    private String avatarUrl;

    private Double ratingAverage = 0.0;

    private Integer reviewsCount = 0;

    private String address;

    private String city;

    private Double pricePerVisit;

    private String about;

    private Integer patientsCount;

}
