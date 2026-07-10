package com.gymtracker.gymtracker.dto.sessionExercise;

import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.SessionExercise;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record SessionExerciseResponse(Long id, Long sessionId, Long exerciseId, String exerciseName,
                                       Integer orderIndex, List<WorkoutSetResponse> workoutSets) {

    public static SessionExerciseResponse from(SessionExercise sessionExercise) {
        return from(sessionExercise, Set.of());
    }

    public static SessionExerciseResponse from(SessionExercise sessionExercise, Set<Long> prWorkoutSetIds) {
        return new SessionExerciseResponse(
                sessionExercise.getId(),
                sessionExercise.getSession().getId(),
                sessionExercise.getExercise().getId(),
                sessionExercise.getExercise().getName(),
                sessionExercise.getOrderIndex(),
                sessionExercise.getWorkoutSets().stream()
                        .map(set -> WorkoutSetResponse.from(set, prWorkoutSetIds))
                        .collect(Collectors.toList())
        );
    }
}