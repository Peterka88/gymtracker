package com.gymtracker.gymtracker.config;

import com.gymtracker.gymtracker.entity.*;
import com.gymtracker.gymtracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ExerciseRepository exerciseRepository;

    @Override
    public void run(String... args) {
        ensureExercises();
    }

    private void ensureExercises() {
        Map<String, MuscleGroup> definitions = new HashMap<>();
        definitions.put("Bench Press", MuscleGroup.CHEST);
        definitions.put("Incline Dumbbell Press", MuscleGroup.CHEST);
        definitions.put("Overhead Press", MuscleGroup.SHOULDERS);
        definitions.put("Tricep Pushdown", MuscleGroup.TRICEPS);
        definitions.put("Pull-up", MuscleGroup.BACK);
        definitions.put("Barbell Row", MuscleGroup.BACK);
        definitions.put("Bicep Curl", MuscleGroup.BICEPS);
        definitions.put("Squat", MuscleGroup.QUADRICEPS);
        definitions.put("Romanian Deadlift", MuscleGroup.HAMSTRINGS);
        definitions.put("Leg Press", MuscleGroup.QUADRICEPS);
        definitions.put("Deadlift", MuscleGroup.BACK);
        definitions.put("Lat Pulldown", MuscleGroup.BACK);
        definitions.put("Dumbbell Shoulder Press", MuscleGroup.SHOULDERS);
        definitions.put("Lateral Raise", MuscleGroup.SHOULDERS);
        definitions.put("Hammer Curl", MuscleGroup.BICEPS);
        definitions.put("Skull Crusher", MuscleGroup.TRICEPS);
        definitions.put("Dips", MuscleGroup.TRICEPS);
        definitions.put("Leg Curl", MuscleGroup.HAMSTRINGS);
        definitions.put("Leg Extension", MuscleGroup.QUADRICEPS);
        definitions.put("Hip Thrust", MuscleGroup.GLUTES);
        definitions.put("Calf Raise", MuscleGroup.CALVES);
        definitions.put("Plank", MuscleGroup.CORE);
        definitions.put("Farmer's Carry", MuscleGroup.FOREARMS);
        definitions.put("Burpee", MuscleGroup.FULL_BODY);

        for (Map.Entry<String, MuscleGroup> entry : definitions.entrySet()) {
            if (exerciseRepository.findByName(entry.getKey()) == null) {
                exerciseRepository.save(exercise(entry.getKey(), entry.getValue()));
            }
        }
    }

    private Exercise exercise(String name, MuscleGroup muscleGroup) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setMuscleGroup(muscleGroup);
        return exercise;
    }
}