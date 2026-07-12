package com.gymtracker.gymtracker.dto.exercise;

import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.MuscleGroup;

public record ExerciseResponseDTO (
        Long id,
        String name,
        MuscleGroup muscleGroup
) {
    public static ExerciseResponseDTO from(Exercise exercise) {
        return new ExerciseResponseDTO(exercise.getId(), exercise.getName(), exercise.getMuscleGroup());
    }
}
