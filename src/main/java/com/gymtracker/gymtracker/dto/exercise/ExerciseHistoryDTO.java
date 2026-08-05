package com.gymtracker.gymtracker.dto.exercise;

import java.time.LocalDate;

public record ExerciseHistoryDTO(
        Long id,
        String name,
        Boolean pr,
        LocalDate date,
        Double bestWeight,
        Integer setCount,
        Integer totalReps,
        Double volume
) {
    public static ExerciseHistoryDTO create(Long id, String name, Boolean pr, LocalDate date, Double bestWeight, Integer setCount, Integer totalReps, Double volume) {
        return new ExerciseHistoryDTO(id, name, pr, date, bestWeight, setCount, totalReps, volume);
    }
}
