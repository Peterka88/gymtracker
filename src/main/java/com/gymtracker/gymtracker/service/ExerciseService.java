package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.exercise.ExerciseDTO;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.repository.ExerciseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
    }


    public Exercise createExercise(ExerciseDTO dto) {
        Exercise exercise = new Exercise();
        exercise.setName(dto.getName());
        exercise.setMuscleGroup(dto.getMuscleGroup());
        return exerciseRepository.save(exercise);
    }

    public void deleteExercise(Long id) {
        exerciseRepository.deleteById(id);
    }

}
