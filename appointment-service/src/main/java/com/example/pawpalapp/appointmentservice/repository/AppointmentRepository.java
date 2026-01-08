package com.example.pawpalapp.appointmentservice.repository;

import com.example.pawpalapp.appointmentservice.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
