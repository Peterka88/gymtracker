package com.gymtracker.gymtracker.dto.exercise;

import com.gymtracker.gymtracker.entity.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciseDTO(
        @NotBlank(message = "Exercise name cannot be blank") String name,
        @NotNull(message = "Muscle group must be specified") MuscleGroup muscleGroup
) {
}
