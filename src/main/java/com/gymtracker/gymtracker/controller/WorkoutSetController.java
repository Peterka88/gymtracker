package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.WorkoutSetReqDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.responses.WorkoutCreateSetResDTO;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.service.WorkoutSetService;
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

// TODO: replace @RequestParam Long userId with @AuthenticationPrincipal once JWT is implemented
@Tag(name = "Workout Sets", description = "Manage individual sets within a workout session")
@RestController
public class WorkoutSetController {

    private final WorkoutSetService workoutSetService;

    public WorkoutSetController(WorkoutSetService workoutSetService) {
        this.workoutSetService = workoutSetService;
    }

    @PostMapping("/api/workouts/exercises/{exerciseSessionId}/sets")
    public ResponseEntity<WorkoutCreateSetResDTO> createWorkoutSet(
            @PathVariable Long exerciseSessionId,
            @Parameter(description = "User ID") @RequestParam Long userId,
            @RequestBody @Valid WorkoutSetReqDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutSetService.createWorkoutSet(userId, exerciseSessionId, dto));
    }

    @Operation(summary = "Partially update a workout set")
    @ApiResponse(responseCode = "200", description = "Workout set updated")
    @ApiResponse(responseCode = "404", description = "Workout set not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Workout set not found",
                              "instance": "/api/workout-sets/99"
                            }"""
            )))
    @PutMapping("/api/workout-sets/{setId}")
    public ResponseEntity<WorkoutSetResponse> updateWorkoutSet(
            @RequestParam Long userId,
            @Parameter(description = "Workout set ID") @PathVariable Long setId,
            @RequestBody WorkoutSetReqDTO dto) {
        return ResponseEntity.ok(workoutSetService.updateWorkoutSet(userId, setId, dto));
    }

    @Operation(summary = "Delete a workout set by ID")
    @ApiResponse(responseCode = "204", description = "Workout set deleted")
    @ApiResponse(responseCode = "404", description = "Workout set not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Workout set not found",
                              "instance": "/api/workout-sets/99"
                            }"""
            )))
    @DeleteMapping("/api/workout-sets/{id}")
    public ResponseEntity<Void> deleteWorkoutSet(
            @Parameter(description = "Workout set ID") @PathVariable Long id) {
        workoutSetService.deleteWorkoutSet(id);
        return ResponseEntity.noContent().build();
    }
}