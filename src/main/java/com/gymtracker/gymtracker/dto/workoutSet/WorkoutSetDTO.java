package com.gymtracker.gymtracker.dto.workoutSet;

import jakarta.validation.constraints.NotNull;

public class WorkoutSetDTO {

    @NotNull(message = "Session exercise must be specified")
    private Long sessionExerciseId;

    private Double weight;

    private Integer reps;

    private String note;

    public Long getSessionExerciseId() {
        return sessionExerciseId;
    }

    public void setSessionExerciseId(Long sessionExerciseId) {
        this.sessionExerciseId = sessionExerciseId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}