package com.gymtracker.gymtracker.dto.sessionExercise;

import jakarta.validation.constraints.NotNull;

public class SessionExerciseDTO {

    @NotNull(message = "Exercise must be specified")
    private Long exerciseId;

    private Integer orderIndex;

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}