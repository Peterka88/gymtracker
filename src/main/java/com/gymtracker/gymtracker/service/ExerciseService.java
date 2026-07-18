package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.common.PageResponse;
import com.gymtracker.gymtracker.dto.exercise.ExerciseDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseResponseDTO;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ExerciseRepository exerciseRepository;

    public PageResponse<ExerciseResponseDTO> getAllExercises(Integer paramSize, Integer page) {
        int size = (paramSize == null || paramSize == 0) ? DEFAULT_PAGE_SIZE : paramSize;
        Pageable pageable = PageRequest.of(page, size);

        Page<ExerciseResponseDTO> result = exerciseRepository.findAll(pageable)
                .map(ExerciseResponseDTO::from);

        return PageResponse.from(result);
    }

    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
    }


    public Exercise createExercise(ExerciseDTO dto) {
        Exercise exercise = new Exercise();
        exercise.setName(dto.name());
        exercise.setMuscleGroup(dto.muscleGroup());
        return exerciseRepository.save(exercise);
    }

    public void deleteExercise(Long id) {
        exerciseRepository.deleteById(id);
    }

}
