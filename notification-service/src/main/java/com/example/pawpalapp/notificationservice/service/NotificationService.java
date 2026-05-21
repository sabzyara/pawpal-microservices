package com.example.pawpalapp.notificationservice.service;

import com.example.pawpalapp.notificationservice.dto.ReminderRequestDto;
import com.example.pawpalapp.notificationservice.model.Notification;
import com.example.pawpalapp.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public void schedule(ReminderRequestDto dto) {

        Notification notification = Notification.builder()
                .userId(dto.getUserId())
                .type(dto.getType())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .scheduledAt(dto.getScheduledAt())
                .read(false)
                .delivered(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);
    }

    public List<Notification> getPending(LocalDateTime now) {
        return repository
                .findByScheduledAtBeforeAndDeliveredFalse(now);
    }

    public void markAsRead(Long id) {
        Notification notification =
                repository.findById(id).orElseThrow();

        notification.setRead(true);

        repository.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsDelivered(Notification notification) {

        notification.setDelivered(true);

        repository.save(notification);
    }
}
