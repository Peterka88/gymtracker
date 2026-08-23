package com.gymtracker.gymtracker.dto.sessionExercise;

import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.SessionExercise;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record SessionExerciseResponse(
        Long id,
        Long exerciseId,
        String exerciseName,
        Integer orderIndex,
        String note,
        Double lastWeight,
        Integer lastReps,
        List<WorkoutSetResponse> workoutSets
) {

    public static SessionExerciseResponse from(SessionExercise sessionExercise, Set<Long> prWorkoutSetIds, Double lastWeight, Integer lastReps) {
        return new SessionExerciseResponse(
                sessionExercise.getId(),
                sessionExercise.getExercise().getId(),
                sessionExercise.getExercise().getName(),
                sessionExercise.getOrderIndex(),
                sessionExercise.getNote(),
                lastWeight,
                lastReps,
                sessionExercise.getWorkoutSets().stream()
                        .map(set -> WorkoutSetResponse.from(set, prWorkoutSetIds))
                        .collect(Collectors.toList())
        );
    }
}