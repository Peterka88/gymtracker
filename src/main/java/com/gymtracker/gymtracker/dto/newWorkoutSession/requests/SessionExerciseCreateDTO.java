package com.gymtracker.gymtracker.dto.newWorkoutSession.requests;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class SessionExerciseCreateDTO {

    @NotEmpty(message = "exerciseIds must not be empty")
    private List<Long> exerciseIds;

    public List<Long> getExerciseIds() {
        return exerciseIds;
    }

}