package com.gymtracker.gymtracker.dto.newWorkoutSession.responses;

public record WorkoutSessionStartResult(
        WorkoutSessionStartResDTO session,
        boolean created
) {
}