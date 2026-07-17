package com.gymtracker.gymtracker.dto.newWorkoutSession.requests;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SessionExerciseCreateDTO(
        @NotEmpty(message = "exerciseIds must not be empty") List<Long> exerciseIds
) {
}