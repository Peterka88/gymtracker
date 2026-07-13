package com.gymtracker.gymtracker.dto.workoutSet;

import com.gymtracker.gymtracker.entity.WorkoutSet;

import java.util.Set;

public record WorkoutSetResponse(
        Long id,
        Double weight,
        Integer reps,
        boolean pr
) {

    public static WorkoutSetResponse from(WorkoutSet set, boolean isPR) {
        return new WorkoutSetResponse(
                set.getId(),
                set.getWeight(),
                set.getReps(),
                isPR);
    }

    public static WorkoutSetResponse from(WorkoutSet set, Set<Long> prWorkoutSetIds) {
        return from(set, prWorkoutSetIds.contains(set.getId()));
    }
}