package com.gymtracker.gymtracker.dto;

import com.gymtracker.gymtracker.entity.WorkoutSet;

public class WorkoutSetResponse {

    public static WorkoutSetResponse from(WorkoutSet set) {
        return new WorkoutSetResponse(
                set.getId(),
                set.getExercise().getId(),
                set.getExercise().getName(),
                set.getWeight(),
                set.getReps(),
                set.getNote());
    }

    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private Double weight;
    private Integer reps;
    private String note;

    public WorkoutSetResponse(Long id, Long exerciseId, String exerciseName, Double weight, Integer reps, String note) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.reps = reps;
        this.note = note;
    }

    public Long getId() { return id; }
    public Long getExerciseId() { return exerciseId; }
    public String getExerciseName() { return exerciseName; }
    public Double getWeight() { return weight; }
    public Integer getReps() { return reps; }
    public String getNote() { return note; }
}