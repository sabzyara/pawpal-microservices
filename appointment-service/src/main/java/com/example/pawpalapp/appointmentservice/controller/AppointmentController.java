package com.example.pawpalapp.appointmentservice.controller;

import com.example.pawpalapp.appointmentservice.dto.AppointmentCreateDto;
import com.example.pawpalapp.appointmentservice.model.Appointment;
import com.example.pawpalapp.appointmentservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public Appointment create(@RequestBody AppointmentCreateDto dto) {
        return appointmentService.create(dto);
    }
}

