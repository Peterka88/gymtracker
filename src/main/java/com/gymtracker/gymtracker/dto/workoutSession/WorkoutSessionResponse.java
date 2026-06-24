package com.gymtracker.gymtracker.dto.workoutSession;

import com.gymtracker.gymtracker.entity.WorkoutSession;

import java.time.LocalDate;

public class WorkoutSessionResponse {

    private Long id;
    private LocalDate date;
    private String note;

    public WorkoutSessionResponse(Long id, LocalDate date, String note) {
        this.id = id;
        this.date = date;
        this.note = note;
    }

    public static WorkoutSessionResponse from(WorkoutSession workoutSession) {
        return new WorkoutSessionResponse(
                workoutSession.getId(),
                workoutSession.getDate(),
                workoutSession.getNote()
        );
    }

    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }
}
