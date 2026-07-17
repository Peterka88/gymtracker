package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.SessionExerciseCreateDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.SessionExerciseNoteDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.responses.SessionExerciseCreateResDTO;
import com.gymtracker.gymtracker.service.WorkoutSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Session Exercises", description = "Exercises assigned to a workout session, in order")
@RestController
@RequiredArgsConstructor
public class SessionExerciseController {

    private final WorkoutSessionService workoutSessionService;

    @Operation(summary = "Add exercises to a workout session")
    @ApiResponse(responseCode = "201", description = "Exercises added to session")
    @PostMapping("/api/workouts/{sessionId}/exercises")
    public ResponseEntity<List<SessionExerciseCreateResDTO>> createSessionExercise(
            @RequestParam Long userId,
            @Parameter(description = "Session ID") @PathVariable Long sessionId,
            @RequestBody @Valid SessionExerciseCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workoutSessionService.createSessionExercise(userId, sessionId, dto));
    }

    @PatchMapping("/api/session-exercises/{id}")
    public ResponseEntity<Void> updateSessionExerciseNote(
            @RequestParam Long userId, @PathVariable Long id,
            @RequestBody @Valid SessionExerciseNoteDTO dto) {
        workoutSessionService.updateSessionExerciseNote(userId, id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}