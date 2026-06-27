package com.gymtracker.gymtracker.dto.workoutSet;

import com.gymtracker.gymtracker.entity.WorkoutSet;

import java.util.Map;

public record WorkoutSetResponse(Long id, Long exerciseId, String exerciseName,
                                 Double weight, Integer reps, String note, boolean isPR) {

    public static WorkoutSetResponse from(WorkoutSet set) {
        return from(set, false);
    }

    public static WorkoutSetResponse from(WorkoutSet set, boolean isPR) {
        return new WorkoutSetResponse(
                set.getId(),
                set.getExercise().getId(),
                set.getExercise().getName(),
                set.getWeight(),
                set.getReps(),
                set.getNote(),
                isPR);
    }

    public static WorkoutSetResponse from(WorkoutSet set, Map<Long, Double> prWeights) {
        Double prWeight = prWeights.get(set.getExercise().getId());
        boolean isPR = set.getWeight() != null && prWeight != null && set.getWeight().equals(prWeight);
        return from(set, isPR);
    }
}