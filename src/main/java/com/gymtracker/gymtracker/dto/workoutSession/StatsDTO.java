package com.gymtracker.gymtracker.dto.workoutSession;

import com.gymtracker.gymtracker.entity.MuscleGroup;

public record StatsDTO(
        Integer workoutsLast30Days,
        Integer workoutsThisYear,
        Integer prsLast30Days,
        NeglectedMuscleGroupDTO neglectedMuscleGroup,
        Integer daysSinceLastWorkout
) {

    /**
     * @param daysSinceLastTrained null when the muscle group has never been trained
     */
    public record NeglectedMuscleGroupDTO(
            MuscleGroup muscleGroup,
            Integer daysSinceLastTrained
    ) {
    }
}