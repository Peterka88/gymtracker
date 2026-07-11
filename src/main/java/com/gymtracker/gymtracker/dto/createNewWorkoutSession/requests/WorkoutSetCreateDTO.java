package com.gymtracker.gymtracker.dto.createNewWorkoutSession.requests;

public class WorkoutSetCreateDTO {

    private Double weight;

    private Integer reps;

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

}