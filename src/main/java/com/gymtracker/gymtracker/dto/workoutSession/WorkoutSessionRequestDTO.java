package com.gymtracker.gymtracker.dto.workoutSession;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class WorkoutSessionRequestDTO {

    private String name;

    @NotNull(message = "Start time cannot be null")
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime startedAt;

    private Integer durationMinutes;

    private String note;

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getNote() {
        return note;
    }

    public String getName() {
        return name;
    }
}