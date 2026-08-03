package com.gymtracker.gymtracker.dto.exercise;

public record ExerciseInfoDTO(
        Integer totalExercises
) {
    public static ExerciseInfoDTO create(Integer totalExercises) {
        return new ExerciseInfoDTO(totalExercises);
    }
}
