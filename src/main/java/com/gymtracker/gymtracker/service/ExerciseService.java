package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.common.PageResponse;
import com.gymtracker.gymtracker.dto.exercise.*;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.MuscleGroup;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import com.gymtracker.gymtracker.repository.ExerciseRepository;
import com.gymtracker.gymtracker.repository.WorkoutSetRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;

    public PageResponse<ExerciseListResponseDTO> getAll(Integer size, Integer page, String search, List<MuscleGroup> muscleGroupList) {
        Pageable pageable = PageRequest.of(page, size);

        String searchPattern = (search == null || search.isBlank())
                ? null
                : "%" + search.toLowerCase() + "%";

        Page<Exercise> exercises;
        exercises = (muscleGroupList == null || muscleGroupList.isEmpty())
                ? exerciseRepository.search(searchPattern, null, pageable)
                : exerciseRepository.search(searchPattern, muscleGroupList, pageable);
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

    public Integer countExercises() {
        return (int) exerciseRepository.count();
    }

    public Exercise updateExercise(Long id, @Valid ExerciseDTO dto) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));

        if (exerciseRepository.findByName(dto.name()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise with this name already exists");
        }

        exercise.setName(dto.name());
        exercise.setMuscleGroup(dto.muscleGroup());
        exercise.setEquipment(dto.equipment());

        return exerciseRepository.save(exercise);
    }

    public ExerciseStatsDTO getExerciseStats(Long id, Long userId) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));

        List<WorkoutSet> sets = workoutSetRepository.findAllForExerciseAndAppUser(id, userId);

        Double pr = sets.stream()
                .max(Comparator.comparing(WorkoutSet::getWeight))
                .map(WorkoutSet::getWeight).orElse(null);

        Map<Long, List<WorkoutSet>> sessionSets = sets.stream()
                .collect(Collectors.groupingBy(set -> set.getSessionExercise().getSession().getId()));

        List<ProgressData> progressData = sessionSets.values().stream()
                .map(ws -> {
                    WorkoutSet topSet = ws.stream()
                            .max(Comparator.comparing(WorkoutSet::getWeight))
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No sets found for session"));

                    double volume = ws.stream()
                            .mapToDouble(s -> s.getWeight() * s.getReps())
                            .sum();

                    return ProgressData.create(
                            topSet.getSessionExercise().getSession().getStartedAt().toLocalDate(),
                            topSet.getWeight(),
                            volume,
                            estimated1RM(topSet)
                    );
                }).toList();


        return ExerciseStatsDTO.create(
                exercise.getId(),
                exercise.getName(),
                pr,
                sets.stream()
                    .max(Comparator.comparing((WorkoutSet s) -> s.getSessionExercise().getSession().getStartedAt())
                            .thenComparing(WorkoutSet::getWeight))
                    .map(WorkoutSet::getWeight)
                    .orElse(null),
                sessionSets.size(),
                progressData
        );
    }

    private Double estimated1RM(WorkoutSet topSet) {
        if (topSet.getReps() == null || topSet.getWeight() == null) {
            return null;
        }
        // Epley formula: 1RM = weight * (1 + reps / 30)
        return topSet.getWeight() * (1 + topSet.getReps() / 30.0);
    }

}
