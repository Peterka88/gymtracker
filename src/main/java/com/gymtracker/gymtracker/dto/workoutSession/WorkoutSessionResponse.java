package com.gymtracker.gymtracker.dto.workoutSession;

import com.gymtracker.gymtracker.entity.WorkoutSession;

import java.time.LocalDate;

public record WorkoutSessionResponse(
        Long id,
        String name,
        LocalDate date,
        Integer exercises,
        boolean pr
) {

    public static WorkoutSessionResponse from(WorkoutSession workoutSession) {
        return from(workoutSession, workoutSession.getSessionExercises().size(), false);
    }

    public static WorkoutSessionResponse from(WorkoutSession workoutSession, int exercises, boolean pr) {
        return new WorkoutSessionResponse(
                workoutSession.getId(),
                workoutSession.getName(),
                workoutSession.getStartedAt().toLocalDate(),
                exercises,
                pr);
    }
}