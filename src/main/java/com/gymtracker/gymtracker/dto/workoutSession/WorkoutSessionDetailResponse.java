package com.gymtracker.gymtracker.dto.workoutSession;

import com.gymtracker.gymtracker.dto.sessionExercise.SessionExerciseResponse;

import java.time.LocalDate;
import java.util.List;

public record WorkoutSessionDetailResponse(Long id, LocalDate date, String note, List<SessionExerciseResponse> exercises) {
}