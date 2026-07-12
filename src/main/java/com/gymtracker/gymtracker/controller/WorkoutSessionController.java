package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.newWorkoutSession.responses.WorkoutSessionStartResDTO;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDetailResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionResponse;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.service.WorkoutSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Workout Sessions", description = "Manage training sessions")
@RestController
@RequestMapping("/api/workouts")
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    public WorkoutSessionController(WorkoutSessionService workoutSessionService) {
        this.workoutSessionService = workoutSessionService;
    }

    @Operation(summary = "Create a new workout session")
    @ApiResponse(responseCode = "201", description = "Session created")
    @PostMapping
    public ResponseEntity<WorkoutSessionStartResDTO> createWorkoutSession(
            @RequestParam Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutSessionService.createWorkoutSession(userId));
    }

    @Operation(summary = "Get all workout sessions")
    @ApiResponse(responseCode = "200", description = "List of all sessions")
    @GetMapping()
    public ResponseEntity<List<WorkoutSessionResponse>> getWorkoutSessions(
            @RequestParam Long userId,
            @Parameter(description = "Page size, defaults to 10 when omitted") @RequestParam(required = false) Integer size,
            @Parameter(description = "Page number, defaults to 0") @RequestParam(required = false) Integer page
    ) {
        return ResponseEntity.ok(workoutSessionService.getWorkoutSessions(userId, size, page));
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
                              "instance": "/api/workouts/99"
                            }"""
            )))
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionDetailResponse> getWorkoutSessionById(
            @RequestParam Long userId,
            @Parameter(description = "Workout Session ID") @PathVariable Long id) {
        return ResponseEntity.ok(workoutSessionService.getWorkoutSessionDetail(userId, id));
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
            @PathVariable Long userId,
            @Parameter(description = "Session ID") @PathVariable Long id) {
        return ResponseEntity.ok(workoutSessionService.getWorkoutSetsBySessionId(userId ,id));
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
            @PathVariable Long userId,
            @Parameter(description = "Session ID") @PathVariable Long id) {
        workoutSessionService.deleteWorkoutSession(userId, id);
        return ResponseEntity.noContent().build();
    }
}