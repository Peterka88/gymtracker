package com.gymtracker.gymtracker.dto.workoutSession;

import com.gymtracker.gymtracker.entity.WorkoutSession;

import java.time.LocalDate;

public record WorkoutSessionResponse(Long id, LocalDate date, Integer exercises, boolean pr) {

    public static WorkoutSessionResponse from(WorkoutSession workoutSession) {
        return from(workoutSession, false);
    }

    public static WorkoutSessionResponse from(WorkoutSession workoutSession, boolean pr) {
        return new WorkoutSessionResponse(
                workoutSession.getId(),
                workoutSession.getDate(),
                workoutSession.getSessionExercises().size(),
                pr);
    }
}