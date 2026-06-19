package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.WorkoutSetDTO;
import com.gymtracker.gymtracker.dto.WorkoutSetPatchDTO;
import com.gymtracker.gymtracker.dto.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import com.gymtracker.gymtracker.repository.WorkoutSetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutSetService {

    private final WorkoutSetRepository workoutSetRepository;
    private final ExerciseService exerciseService;
    private final WorkoutSessionService workoutSessionService;

    public WorkoutSetService(WorkoutSetRepository workoutSetRepository,
                             ExerciseService exerciseService,
                             WorkoutSessionService workoutSessionService) {
        this.workoutSetRepository = workoutSetRepository;
        this.exerciseService = exerciseService;
        this.workoutSessionService = workoutSessionService;
    }

    public WorkoutSetResponse createWorkoutSet(WorkoutSetDTO dto) {
        return toResponse(workoutSetRepository.save(mapDtoToSet(new WorkoutSet(), dto)));
    }

    public List<WorkoutSetResponse> getAllWorkoutSets() {
        return workoutSetRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkoutSetResponse getWorkoutSetById(Long id) {
        return toResponse(workoutSetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout set not found")));
    }

    public WorkoutSetResponse partialUpdateWorkoutSet(Long id, WorkoutSetPatchDTO dto) {
        WorkoutSet set = workoutSetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout set not found"));
        if (dto.getExerciseId() != null)
            set.setExercise(exerciseService.getExerciseById(dto.getExerciseId()));
        if (dto.getWorkoutSessionId() != null)
            set.setSession(workoutSessionService.getWorkoutSessionById(dto.getWorkoutSessionId()));
        if (dto.getWeight() != null)
            set.setWeight(dto.getWeight());
        if (dto.getReps() != null)
            set.setReps(dto.getReps());
        if (dto.getNote() != null)
            set.setNote(dto.getNote());
        return toResponse(workoutSetRepository.save(set));
    }

    public void deleteWorkoutSet(Long id) {
        workoutSetRepository.deleteById(id);
    }

    private WorkoutSet mapDtoToSet(WorkoutSet set, WorkoutSetDTO dto) {
        set.setExercise(exerciseService.getExerciseById(dto.getExerciseId()));
        set.setSession(workoutSessionService.getWorkoutSessionById(dto.getSessionId()));
        set.setWeight(dto.getWeight());
        set.setReps(dto.getReps());
        set.setNote(dto.getNote());
        return set;
    }

    private WorkoutSetResponse toResponse(WorkoutSet set) {
        return WorkoutSetResponse.from(set);
    }
}