package com.gymtracker.gymtracker.dto.workoutSet;

import com.gymtracker.gymtracker.entity.WorkoutSet;

import java.util.Map;

public class WorkoutSetResponse {

    public static WorkoutSetResponse from(WorkoutSet set) {
        return from(set, false);
    }

    public static WorkoutSetResponse from(WorkoutSet set, boolean isPR) {
        return new WorkoutSetResponse(
                set.getId(),
                set.getExercise().getId(),
                set.getExercise().getName(),
                set.getWeight(),
                set.getReps(),
                set.getNote(),
                isPR);
    }

    public static WorkoutSetResponse from(WorkoutSet set, Map<Long, Double> prWeights) {
        Double prWeight = prWeights.get(set.getExercise().getId());
        boolean isPR = set.getWeight() != null && prWeight != null && set.getWeight().equals(prWeight);
        return from(set, isPR);
    }

    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private Double weight;
    private Integer reps;
    private String note;
    private boolean isPR;

    public WorkoutSetResponse(Long id, Long exerciseId, String exerciseName,
                              Double weight, Integer reps, String note, boolean isPR) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.reps = reps;
        this.note = note;
        this.isPR = isPR;
    }

    public Long getId() { return id; }
    public Long getExerciseId() { return exerciseId; }
    public String getExerciseName() { return exerciseName; }
    public Double getWeight() { return weight; }
    public Integer getReps() { return reps; }
    public String getNote() { return note; }
    public boolean isPR() { return isPR; }
}