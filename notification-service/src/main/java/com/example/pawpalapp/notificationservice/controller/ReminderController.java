package com.example.pawpalapp.notificationservice.controller;

import com.example.pawpalapp.notificationservice.dto.ReminderRequestDto;
import com.example.pawpalapp.notificationservice.model.Notification;
import com.example.pawpalapp.notificationservice.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(
            @PathVariable Long userId
    ) {
        return service.getUserNotifications(userId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
    }
}
