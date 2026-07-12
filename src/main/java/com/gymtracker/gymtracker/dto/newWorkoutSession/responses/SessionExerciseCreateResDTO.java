package com.gymtracker.gymtracker.dto.newWorkoutSession.responses;

import com.gymtracker.gymtracker.entity.SessionExercise;

public record SessionExerciseCreateResDTO (
        Long id,
        Long exerciseId,
        Integer orderIndex
) {
    public static SessionExerciseCreateResDTO from(SessionExercise sessionExercise) {
        return new SessionExerciseCreateResDTO(sessionExercise.getId(), sessionExercise.getExercise().getId(), sessionExercise.getOrderIndex());
    }
}
