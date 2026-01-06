package com.example.pawpalapp.petmanagementservice.repository;

import com.example.pawpalapp.petmanagementservice.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByPetId(Long petId);
}
