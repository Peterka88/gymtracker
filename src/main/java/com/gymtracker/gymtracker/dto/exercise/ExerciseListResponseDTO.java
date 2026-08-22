package com.gymtracker.gymtracker.dto.exercise;

import com.gymtracker.gymtracker.entity.Equipment;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.MuscleGroup;

import java.time.LocalDate;
import java.util.List;

public record ExerciseListResponseDTO (
        Long id,
        String name,
        MuscleGroup muscleGroup,
        Equipment equipment,
        LocalDate lastDate,
        Double lastWeight,
        List<Double> lastWeights
) {
    public static ExerciseListResponseDTO from(Exercise exercise, LocalDate lastDate, Double lastWeight, List<Double> lastWeights) {
        return new ExerciseListResponseDTO(exercise.getId(), exercise.getName(), exercise.getMuscleGroup(), exercise.getEquipment(), lastDate, lastWeight, lastWeights);
    }

}
