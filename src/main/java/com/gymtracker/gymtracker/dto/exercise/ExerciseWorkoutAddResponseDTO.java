package com.gymtracker.gymtracker.dto.exercise;

import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.MuscleGroup;

public record ExerciseWorkoutAddResponseDTO(
        Long id,
        String name,
        MuscleGroup muscleGroup
) {
    public static ExerciseWorkoutAddResponseDTO from(Exercise exercise) {
        return new ExerciseWorkoutAddResponseDTO(exercise.getId(), exercise.getName(), exercise.getMuscleGroup());
    }
}
