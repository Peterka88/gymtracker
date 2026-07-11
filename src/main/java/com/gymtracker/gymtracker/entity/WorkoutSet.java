package com.gymtracker.gymtracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_sets")
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sessionExerciseId", nullable = false)
    private SessionExercise sessionExercise;

    private Double weight;

    private Integer reps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SessionExercise getSessionExercise() {
        return sessionExercise;
    }

    public void setSessionExercise(SessionExercise sessionExercise) {
        this.sessionExercise = sessionExercise;
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
}