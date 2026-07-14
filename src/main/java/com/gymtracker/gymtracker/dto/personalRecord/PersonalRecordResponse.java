package com.gymtracker.gymtracker.dto.personalRecord;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gymtracker.gymtracker.entity.PersonalRecord;

import java.time.LocalDateTime;

public record PersonalRecordResponse(Long id, Long userId, Long exerciseId, String exerciseName,
                                     Double weight, Integer reps,
                                     @JsonFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime achievedAt,
                                     Long workoutSetId) {

    public static PersonalRecordResponse from(PersonalRecord pr) {
        return new PersonalRecordResponse(
                pr.getId(),
                pr.getAppUser() != null ? pr.getAppUser().getId() : null,
                pr.getExercise().getId(),
                pr.getExercise().getName(),
                pr.getWorkoutSet().getWeight(),
                pr.getWorkoutSet().getReps(),
                pr.getWorkoutSet().getSessionExercise().getSession().getStartedAt(),
                pr.getWorkoutSet() != null ? pr.getWorkoutSet().getId() : null
        );
    }
}