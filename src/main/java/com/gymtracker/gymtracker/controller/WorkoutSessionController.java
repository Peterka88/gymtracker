package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDetailResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDTO;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionResponse;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.service.WorkoutSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Workout Sessions", description = "Manage training sessions")
@RestController
@RequestMapping("/api/workout-sessions")
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    public WorkoutSessionController(WorkoutSessionService workoutSessionService) {
        this.workoutSessionService = workoutSessionService;
    }

    @Operation(summary = "Create a new workout session")
    @ApiResponse(responseCode = "201", description = "Session created")
    @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "date": "Date cannot be null"
                            }"""
            )))
    @PostMapping
    public ResponseEntity<WorkoutSessionResponse> createWorkoutSession(@RequestBody @Valid WorkoutSessionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutSessionService.saveWorkoutSession(dto));
    }

    @Operation(summary = "Get all workout sessions")
    @ApiResponse(responseCode = "200", description = "List of all sessions")
    @GetMapping
    public ResponseEntity<List<WorkoutSessionResponse>> getAllWorkoutSessions() {
        return ResponseEntity.ok(workoutSessionService.getAllWorkoutSessions());
    }

    @Operation(summary = "Get workout session detail with all sets")
    @ApiResponse(responseCode = "200", description = "Session detail including workout sets")
    @ApiResponse(responseCode = "404", description = "Session not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Workout session not found",
                              "instance": "/api/workout-sessions/99"
                            }"""
            )))
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionDetailResponse> getWorkoutSessionById(
            @Parameter(description = "Session ID") @PathVariable Long id) {
        return ResponseEntity.ok(workoutSessionService.getWorkoutSessionDetail(id));
    }

    @Operation(summary = "Get all workout sets for a session")
    @ApiResponse(responseCode = "200", description = "List of workout sets")
    @ApiResponse(responseCode = "404", description = "Session not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Workout session not found",
                              "instance": "/api/workout-sessions/99/workout-sets"
                            }"""
            )))
    @GetMapping("/{id}/workout-sets")
    public ResponseEntity<List<WorkoutSetResponse>> getWorkoutSetsBySessionId(
            @Parameter(description = "Session ID") @PathVariable Long id) {
        return ResponseEntity.ok(workoutSessionService.getWorkoutSetsBySessionId(id));
    }

    @Operation(summary = "Delete a workout session and all its sets")
    @ApiResponse(responseCode = "204", description = "Session deleted")
    @ApiResponse(responseCode = "404", description = "Session not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Workout session not found",
                              "instance": "/api/workout-sessions/99"
                            }"""
            )))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(
            @Parameter(description = "Session ID") @PathVariable Long id) {
        workoutSessionService.deleteWorkoutSession(id);
        return ResponseEntity.noContent().build();
    }
}