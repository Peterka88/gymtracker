package com.gymtracker.gymtracker.dto.newWorkoutSession.responses;

import com.gymtracker.gymtracker.entity.WorkoutSession;

import java.time.LocalDateTime;

public record WorkoutSessionPatchResDTO (
        Long id,
        String name,
        String note,
        LocalDateTime startedAt
) {
    public static WorkoutSessionPatchResDTO from(WorkoutSession session) {
        return new WorkoutSessionPatchResDTO(
                session.getId(),
                session.getName(),
                session.getNote(),
                session.getStartedAt()
        );
    }
}
