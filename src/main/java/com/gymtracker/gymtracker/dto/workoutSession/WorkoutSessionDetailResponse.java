package com.gymtracker.gymtracker.dto.workoutSession;

import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;

import java.time.LocalDate;
import java.util.List;

public class WorkoutSessionDetailResponse {

    private Long id;
    private LocalDate date;
    private String note;
    private List<WorkoutSetResponse> workoutSets;

    public WorkoutSessionDetailResponse(Long id, LocalDate date, String note, List<WorkoutSetResponse> workoutSets) {
        this.id = id;
        this.date = date;
        this.note = note;
        this.workoutSets = workoutSets;
    }

    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }
    public List<WorkoutSetResponse> getWorkoutSets() { return workoutSets; }
}