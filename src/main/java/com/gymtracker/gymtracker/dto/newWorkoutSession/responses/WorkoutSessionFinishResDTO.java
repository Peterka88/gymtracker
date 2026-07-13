package com.gymtracker.gymtracker.dto.newWorkoutSession.responses;

import com.gymtracker.gymtracker.entity.WorkoutSession;

import java.time.LocalDateTime;

public record WorkoutSessionFinishResDTO(
        Long id,
        String name,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Integer durationMinutes
) {
    public static WorkoutSessionFinishResDTO from(WorkoutSession session) {
        return new WorkoutSessionFinishResDTO(
                session.getId(),
                session.getName(),
                session.getStartedAt(),
                session.getEndedAt(),
                session.getDurationMinutes()
        );
    }
}