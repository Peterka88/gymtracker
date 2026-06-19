package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.WorkoutSetDTO;
import com.gymtracker.gymtracker.dto.WorkoutSetPatchDTO;
import com.gymtracker.gymtracker.dto.WorkoutSetResponse;
import com.gymtracker.gymtracker.service.WorkoutSetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-sets")
public class WorkoutSetController {

    private final WorkoutSetService workoutSetService;

    public WorkoutSetController(WorkoutSetService workoutSetService) {
        this.workoutSetService = workoutSetService;
    }

    @PostMapping
    public ResponseEntity<WorkoutSetResponse> createWorkoutSet(@RequestBody @Valid WorkoutSetDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutSetService.createWorkoutSet(dto));
    }

    @GetMapping
    public ResponseEntity<List<WorkoutSetResponse>> getAllWorkoutSets() {
        return ResponseEntity.ok(workoutSetService.getAllWorkoutSets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSetResponse> getWorkoutSetById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutSetService.getWorkoutSetById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WorkoutSetResponse> partialUpdateWorkoutSet(@PathVariable Long id, @RequestBody WorkoutSetPatchDTO dto) {
        return ResponseEntity.ok(workoutSetService.partialUpdateWorkoutSet(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutSet(@PathVariable Long id) {
        workoutSetService.deleteWorkoutSet(id);
        return ResponseEntity.noContent().build();
    }
}