package com.gymtracker.gymtracker.dto.exercise;

import com.gymtracker.gymtracker.entity.Equipment;
import com.gymtracker.gymtracker.entity.MuscleGroup;

import java.util.List;

public record ExerciseStatsDTO(
        Long id,
        String name,
        MuscleGroup muscleGroup,
        Equipment equipment,
        Double pr,
        Double lastTraining,
        Integer totalWorkouts,
        List<ProgressData> progressData
) {
    public static ExerciseStatsDTO create(Long id, String name, MuscleGroup muscleGroup, Equipment equipment, Double pr, Double lastTraining, Integer totalWorkouts, List<ProgressData> progressData) {
        return new ExerciseStatsDTO(id, name, muscleGroup, equipment, pr, lastTraining, totalWorkouts, progressData);
    }
}
