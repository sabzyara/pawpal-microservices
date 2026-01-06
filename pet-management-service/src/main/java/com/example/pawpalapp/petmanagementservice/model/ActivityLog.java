package com.example.pawpalapp.petmanagementservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @Column(nullable = false)
    private Long petId;

    private LocalDate date;

    private String activityType;

    private int distance;

    private int durationInMinutes;

    /* === domain methods === */

    public void addActivity() {
        // domain logic if needed
    }

    public void editActivity() {
        // domain logic if needed
    }

    public int calculateActivityStats() {
        return distance * durationInMinutes;
    }

    public String generateActivitySummary() {
        return activityType + " for " + durationInMinutes + " minutes";
    }

    // getters & setters
}
