package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.common.PageResponse;
import com.gymtracker.gymtracker.dto.exercise.ExerciseDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseListResponseDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseWorkoutAddResponseDTO;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.repository.ExerciseRepository;
import com.gymtracker.gymtracker.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;

    public PageResponse<ExerciseListResponseDTO> getAll(Integer size, Integer page) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Exercise> exercises = exerciseRepository.findAll(pageable);
        List<Long> ids = exercises.stream().map((Exercise::getId)).toList();
        Map<Long, WorkoutSetRepository.LastPerformedProjection> lastPerformedProjectionMap;

        if (!ids.isEmpty()) {
            lastPerformedProjectionMap = workoutSetRepository.findLastPerformedByExercise(ids).stream()
                    .collect(Collectors.toMap(WorkoutSetRepository.LastPerformedProjection::getExerciseId, exercise -> exercise));
        } else {
            lastPerformedProjectionMap = Map.of();
        }

        Page<ExerciseListResponseDTO> result = exercises
                .map(exercise -> {
                    var projection = lastPerformedProjectionMap.get(exercise.getId());
                    if (projection == null){
                        return ExerciseListResponseDTO.from(exercise, null, null);
                    }
                    LocalDateTime lastDate = projection.getLastDate();
                    Double lastWeight = projection.getLastWeight();
                    return ExerciseListResponseDTO.from(exercise, lastDate.toLocalDate(), lastWeight);
                });

        return PageResponse.from(result);
    }

    public PageResponse<ExerciseWorkoutAddResponseDTO> getAllForWorkout(Integer size, Integer page) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ExerciseWorkoutAddResponseDTO> result = exerciseRepository.findAll(pageable)
                .map(ExerciseWorkoutAddResponseDTO::from);

        return PageResponse.from(result);
    }

    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
    }


    public Exercise createExercise(ExerciseDTO dto) {
        if (exerciseRepository.findByName(dto.name()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise with this name already exists");
        }

        Exercise exercise = new Exercise();
        exercise.setName(dto.name());
        exercise.setMuscleGroup(dto.muscleGroup());
        exercise.setEquipment(dto.equipment());
        return exerciseRepository.save(exercise);
    }

    public void deleteExercise(Long id) {
        exerciseRepository.deleteById(id);
    }

}
