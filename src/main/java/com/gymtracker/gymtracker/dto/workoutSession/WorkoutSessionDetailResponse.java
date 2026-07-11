package com.gymtracker.gymtracker.dto.workoutSession;

import com.gymtracker.gymtracker.dto.sessionExercise.SessionExerciseResponse;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutSessionDetailResponse(
        Long id,
        String name,
        LocalDateTime startedAt,
        Integer duration,
        String note,
        boolean pr,
        List<SessionExerciseResponse> sessionExercises
) {}