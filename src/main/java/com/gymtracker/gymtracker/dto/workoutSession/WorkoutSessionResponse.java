package com.gymtracker.gymtracker.dto.workoutSession;

import com.gymtracker.gymtracker.entity.WorkoutSession;

import java.time.LocalDate;

public record WorkoutSessionResponse(Long id, LocalDate date, String note) {

    public static WorkoutSessionResponse from(WorkoutSession workoutSession) {
        return new WorkoutSessionResponse(workoutSession.getId(), workoutSession.getDate(), workoutSession.getNote());
    }
}