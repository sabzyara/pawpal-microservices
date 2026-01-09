package com.example.pawpalapp.notificationservice.scheduler;

import com.example.pawpalapp.notificationservice.model.Notification;
import com.example.pawpalapp.notificationservice.service.NotificationService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class ReminderScheduler {

    private final NotificationService service;

    public ReminderScheduler(NotificationService service) {
        this.service = service;
    }

    @Scheduled(fixedRate = 60000)
    public void sendReminders() {

        List<Notification> due =
                service.getPending(LocalDateTime.now());

        for (Notification n : due) {
            System.out.println(
                    "REMINDER → user " + n.getUserId()
                            + ": " + n.getTitle()
            );

            service.markAsRead(n);
        }
    }
}
