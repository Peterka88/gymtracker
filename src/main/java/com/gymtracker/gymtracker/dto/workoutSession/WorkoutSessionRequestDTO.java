package com.gymtracker.gymtracker.dto.workoutSession;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class WorkoutSessionRequestDTO {

    private String name;

    @NotNull(message = "Date cannot be null")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;

    private String note;

    public LocalDate getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public String getName() {
        return name;
    }
}
