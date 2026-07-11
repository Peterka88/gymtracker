package com.gymtracker.gymtracker.dto.createNewWorkoutSession.requests;

import jakarta.validation.constraints.NotNull;

public class SessionExerciseCreateDTO {

    @NotNull(message = "Exercise must be specified")
    private Long exerciseId;

    private Integer orderIndex;

    public Long getExerciseId() {
        return exerciseId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

}