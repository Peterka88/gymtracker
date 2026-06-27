package com.gymtracker.gymtracker.dto.personalRecord;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gymtracker.gymtracker.entity.PersonalRecord;

import java.time.LocalDate;

public record PersonalRecordResponse(Long id, Long exerciseId, String exerciseName,
                                     Double weight, Integer reps,
                                     @JsonFormat(pattern = "dd.MM.yyyy") LocalDate achievedAt,
                                     Long workoutSetId) {

    public static PersonalRecordResponse from(PersonalRecord pr) {
        return new PersonalRecordResponse(
                pr.getId(),
                pr.getExercise().getId(),
                pr.getExercise().getName(),
                pr.getWeight(),
                pr.getReps(),
                pr.getAchievedAt(),
                pr.getWorkoutSet() != null ? pr.getWorkoutSet().getId() : null
        );
    }
}