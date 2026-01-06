package com.example.pawpalapp.petmanagementservice.mapper;

import com.example.pawpalapp.petmanagementservice.dto.activity.*;
import com.example.pawpalapp.petmanagementservice.model.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityLog toEntity(ActivityCreateDto dto) {
        ActivityLog log = new ActivityLog();
        log.setPetId(dto.getPetId());
        log.setDate(dto.getDate());
        log.setActivityType(dto.getActivityType());
        log.setDistance(dto.getDistance());
        log.setDurationInMinutes(dto.getDurationInMinutes());
        return log;
    }

    public ActivityResponseDto toDto(ActivityLog log) {
        return ActivityResponseDto.builder()
                .activityId(log.getActivityId())
                .summary(log.generateActivitySummary())
                .build();
    }
}
