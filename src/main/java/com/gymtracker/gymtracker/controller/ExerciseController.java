package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.common.PageResponse;
import com.gymtracker.gymtracker.dto.exercise.ExerciseDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseListResponseDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseWorkoutAddResponseDTO;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Exercises", description = "Manage exercises and their muscle groups")
@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<PageResponse<ExerciseListResponseDTO>> getAll(
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok(exerciseService.getAll(size, page));
    }


    @Operation(summary = "Get all exercises for workout")
    @ApiResponse(responseCode = "200", description = "Page of exercises")
    @GetMapping("/workout")
    public ResponseEntity<PageResponse<ExerciseWorkoutAddResponseDTO>> getAllForWorkout(
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok(exerciseService.getAllForWorkout(size, page));
    }

    @Operation(summary = "Create a new exercise")
    @ApiResponse(responseCode = "201", description = "Exercise created")
    @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "name": "Exercise name cannot be blank",
                              "muscleGroup": "Muscle group must be specified"
                            }"""
            )))
    @PostMapping
    public ResponseEntity<Exercise> create(@RequestBody @Valid ExerciseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseService.createExercise(dto));
    }

    @Operation(summary = "Delete an exercise by ID")
    @ApiResponse(responseCode = "204", description = "Exercise deleted")
    @ApiResponse(responseCode = "404", description = "Exercise not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Exercise not found",
                              "instance": "/api/exercises/99"
                            }"""
            )))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Exercise ID") @PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}