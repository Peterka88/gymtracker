package com.gymtracker.gymtracker.dto.workoutSession;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class WorkoutSessionDTO {

    @NotNull(message = "Date cannot be null")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;

    private String note;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
