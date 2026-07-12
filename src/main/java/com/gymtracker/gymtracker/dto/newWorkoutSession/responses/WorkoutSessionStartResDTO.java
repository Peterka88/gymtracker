package com.gymtracker.gymtracker.dto.newWorkoutSession.responses;

import com.gymtracker.gymtracker.entity.WorkoutSession;

import java.time.LocalDateTime;

public record  WorkoutSessionStartResDTO (
    Long id,
    String name,
    LocalDateTime startedAt
) {
    public static WorkoutSessionStartResDTO from(WorkoutSession session) {
        return new WorkoutSessionStartResDTO(session.getId(), session.getName(), session.getStartedAt());
    }
}
