package com.gymtracker.gymtracker.dto.sessionExercise;

import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.SessionExercise;

import java.util.List;
import java.util.stream.Collectors;

public record SessionExerciseResponse(Long id, Long sessionId, Long exerciseId, String exerciseName,
                                       Integer orderIndex, List<WorkoutSetResponse> workoutSets) {

    public static SessionExerciseResponse from(SessionExercise sessionExercise) {
        return new SessionExerciseResponse(
                sessionExercise.getId(),
                sessionExercise.getSession().getId(),
                sessionExercise.getExercise().getId(),
                sessionExercise.getExercise().getName(),
                sessionExercise.getOrderIndex(),
                sessionExercise.getWorkoutSets().stream()
                        .map(WorkoutSetResponse::from)
                        .collect(Collectors.toList())
        );
    }
}