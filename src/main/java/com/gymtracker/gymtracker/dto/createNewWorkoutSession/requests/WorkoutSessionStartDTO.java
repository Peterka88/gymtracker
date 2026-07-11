package com.gymtracker.gymtracker.dto.createNewWorkoutSession.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class WorkoutSessionStartDTO {

    private String name;

    @NotNull(message = "Start time cannot be null")
    private LocalDateTime startedAt;

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public String getName() {
        return name;
    }
}