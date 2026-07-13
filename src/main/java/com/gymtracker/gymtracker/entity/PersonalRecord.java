package com.gymtracker.gymtracker.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "personal_records")
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exerciseId")
    private Exercise exercise;

    @OneToOne
    @JoinColumn(name = "workoutSetId", unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private WorkoutSet workoutSet;

    @ManyToOne
    @JoinColumn(name = "userId")
    private AppUser appUser;

    private Double weight;

    private Integer reps;

    private LocalDateTime achievedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }
    public WorkoutSet getWorkoutSet() { return workoutSet; }
    public void setWorkoutSet(WorkoutSet workoutSet) { this.workoutSet = workoutSet; }
    public AppUser getAppUser() { return appUser; }
    public void setAppUser(AppUser appUser) { this.appUser = appUser; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
    public LocalDateTime getAchievedAt() { return achievedAt; }
    public void setAchievedAt(LocalDateTime achievedAt) { this.achievedAt = achievedAt; }
}