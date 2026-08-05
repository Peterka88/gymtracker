package com.gymtracker.gymtracker.dto.exercise;

import java.util.List;

public record ExerciseStatsDTO(
        Long id,
        String name,
        Double pr,
        Double lastTraining,
        Integer totalWorkouts,
        List<ProgressData> progressData
) {
    public static ExerciseStatsDTO create(Long id, String name, Double pr, Double lastTraining, Integer totalWorkouts, List<ProgressData> progressData) {
        return new ExerciseStatsDTO(id, name, pr, lastTraining, totalWorkouts, progressData);
    }
}
