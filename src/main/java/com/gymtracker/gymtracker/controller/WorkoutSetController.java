package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetDTO;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetPatchDTO;
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

import java.util.List;

// TODO: replace @RequestParam Long userId with @AuthenticationPrincipal once JWT is implemented
@Tag(name = "Workout Sets", description = "Manage individual sets within a workout session")
@RestController
@RequestMapping("/api/workout-sets")
public class WorkoutSetController {

    private final WorkoutSetService workoutSetService;

    public WorkoutSetController(WorkoutSetService workoutSetService) {
        this.workoutSetService = workoutSetService;
    }

    @Operation(summary = "Log a new workout set", description = "Automatically updates personal record if weight is a new max")
    @ApiResponse(responseCode = "201", description = "Set created, isPR indicates if it's a new personal record")
    @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "sessionExerciseId": "Session exercise must be specified"
                            }"""
            )))
    @ApiResponse(responseCode = "404", description = "Exercise or session not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Exercise not found",
                              "instance": "/api/workout-sets"
                            }"""
            )))
    @PostMapping
    public ResponseEntity<WorkoutSetResponse> createWorkoutSet(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @RequestBody @Valid WorkoutSetDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutSetService.createWorkoutSet(userId, dto));
    }

    @Operation(summary = "Get all workout sets")
    @ApiResponse(responseCode = "200", description = "List of all workout sets")
    @GetMapping
    public ResponseEntity<List<WorkoutSetResponse>> getAllWorkoutSets(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        return ResponseEntity.ok(workoutSetService.getAllWorkoutSets(userId));
    }

    @Operation(summary = "Get a workout set by ID")
    @ApiResponse(responseCode = "200", description = "Workout set found")
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
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSetResponse> getWorkoutSetById(
            @Parameter(description = "Workout set ID") @PathVariable Long id) {
        return ResponseEntity.ok(workoutSetService.getWorkoutSetById(id));
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
    @PatchMapping("/{id}")
    public ResponseEntity<WorkoutSetResponse> partialUpdateWorkoutSet(
            @PathVariable Long userId,
            @Parameter(description = "Workout set ID") @PathVariable Long id,
            @RequestBody WorkoutSetPatchDTO dto) {
        return ResponseEntity.ok(workoutSetService.partialUpdateWorkoutSet(userId, id, dto));
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutSet(
            @Parameter(description = "Workout set ID") @PathVariable Long id) {
        workoutSetService.deleteWorkoutSet(id);
        return ResponseEntity.noContent().build();
    }
}