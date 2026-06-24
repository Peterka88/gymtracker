package com.gymtracker.gymtracker.dto.exercise;

import com.gymtracker.gymtracker.entity.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExerciseDTO {

    @NotBlank(message = "Exercise name cannot be blank")
    private String name;

    @NotNull(message = "Muscle group must be specified")
    private MuscleGroup muscleGroup;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MuscleGroup getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(MuscleGroup muscleGroup) {
        this.muscleGroup = muscleGroup;
    }
}
