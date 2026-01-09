package com.example.pawpalapp.notificationservice.repository;

import com.example.pawpalapp.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByScheduledAtBeforeAndReadFalse(
            LocalDateTime time
    );
}
