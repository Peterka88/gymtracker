package com.gymtracker.gymtracker.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "personal_records")
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exerciseId")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "workoutSetId")
    private WorkoutSet workoutSet;

    private Double weight;

    private Integer reps;

    private LocalDate achievedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }
    public WorkoutSet getWorkoutSet() { return workoutSet; }
    public void setWorkoutSet(WorkoutSet workoutSet) { this.workoutSet = workoutSet; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
    public LocalDate getAchievedAt() { return achievedAt; }
    public void setAchievedAt(LocalDate achievedAt) { this.achievedAt = achievedAt; }
}