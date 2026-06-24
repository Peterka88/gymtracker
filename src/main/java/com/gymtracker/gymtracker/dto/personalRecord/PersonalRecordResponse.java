package com.gymtracker.gymtracker.dto.personalRecord;

import com.gymtracker.gymtracker.entity.PersonalRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class PersonalRecordResponse {

    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private Double weight;
    private Integer reps;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate achievedAt;

    public PersonalRecordResponse(Long id, Long exerciseId, String exerciseName,
                                  Double weight, Integer reps, LocalDate achievedAt) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.reps = reps;
        this.achievedAt = achievedAt;
    }

    public static PersonalRecordResponse from(PersonalRecord pr) {
        return new PersonalRecordResponse(
                pr.getId(),
                pr.getExercise().getId(),
                pr.getExercise().getName(),
                pr.getWeight(),
                pr.getReps(),
                pr.getAchievedAt()
        );
    }

    public Long getId() { return id; }
    public Long getExerciseId() { return exerciseId; }
    public String getExerciseName() { return exerciseName; }
    public Double getWeight() { return weight; }
    public Integer getReps() { return reps; }
    public LocalDate getAchievedAt() { return achievedAt; }
}