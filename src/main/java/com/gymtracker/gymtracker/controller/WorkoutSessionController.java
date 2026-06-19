package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.WorkoutSessionDetailResponse;
import com.gymtracker.gymtracker.dto.WorkoutSessionDTO;
import com.gymtracker.gymtracker.dto.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.WorkoutSession;
import com.gymtracker.gymtracker.service.WorkoutSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-sessions")
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    public WorkoutSessionController(WorkoutSessionService workoutSessionService) {
        this.workoutSessionService = workoutSessionService;
    }

    @PostMapping
    public ResponseEntity<WorkoutSession> createWorkoutSession(@RequestBody @Valid WorkoutSessionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutSessionService.saveWorkoutSession(dto));
    }

    @GetMapping
    public ResponseEntity<List<WorkoutSession>> getAllWorkoutSessions() {
        return ResponseEntity.ok(workoutSessionService.getAllWorkoutSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionDetailResponse> getWorkoutSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutSessionService.getWorkoutSessionDetail(id));
    }

    @GetMapping("/{id}/workout-sets")
    public ResponseEntity<List<WorkoutSetResponse>> getWorkoutSetsBySessionId(@PathVariable Long id) {
        return ResponseEntity.ok(workoutSessionService.getWorkoutSetsBySessionId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        workoutSessionService.deleteWorkoutSession(id);
        return ResponseEntity.noContent().build();
    }
}
