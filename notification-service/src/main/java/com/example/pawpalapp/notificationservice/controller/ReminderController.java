package com.example.pawpalapp.notificationservice.controller;

import com.example.pawpalapp.notificationservice.dto.ReminderRequestDto;
import com.example.pawpalapp.notificationservice.service.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final NotificationService service;

    public ReminderController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public void createReminder(@RequestBody ReminderRequestDto dto) {
        service.schedule(dto);
    }
}
