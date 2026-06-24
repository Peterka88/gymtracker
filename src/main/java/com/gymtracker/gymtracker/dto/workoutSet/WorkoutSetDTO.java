package com.gymtracker.gymtracker.dto.workoutSet;

import jakarta.validation.constraints.NotNull;

public class WorkoutSetDTO {

    @NotNull(message = "Exercise must be specified")
    private Long exerciseId;

    @NotNull(message = "Workout session must be specified")
    private Long sessionId;

    private Double weight;

    private Integer reps;

    private String note;

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
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