package com.gymtracker.gymtracker.dto.workoutSet;

public class WorkoutSetPatchDTO {

    private Long exerciseId;
    private Long workoutSessionId;
    private Double weight;
    private Integer reps;
    private String note;

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public Long getWorkoutSessionId() { return workoutSessionId; }
    public void setWorkoutSessionId(Long workoutSessionId) { this.workoutSessionId = workoutSessionId; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}