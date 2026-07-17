package com.gymtracker.gymtracker.config;

import com.gymtracker.gymtracker.entity.*;
import com.gymtracker.gymtracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final int TARGET_SESSION_COUNT = 24;

    private final AppUserRepository appUserRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WeightLogRepository weightLogRepository;

    @Override
    public void run(String... args) {
        AppUser user = appUserRepository.findByUsername("martin");
        boolean isNewUser = user == null;
        if (isNewUser) {
            user = new AppUser();
            user.setName("Martin Petrina");
            user.setUsername("martin");
            user.setPassword("test1234");
            user.setHeight(180.0);
            user = appUserRepository.save(user);
        }

        Map<String, Exercise> exercises = ensureExercises();

        int existingSessions = workoutSessionRepository
                .findAllByAppUserIdOrderByStartedAtDesc(user.getId(), Pageable.unpaged())
                .size();

        List<SessionTemplate> handPicked = List.of(
                new SessionTemplate("Push deň", List.of(
                        new SeedExercise(exercises.get("Bench Press"), new double[]{60, 70, 80, 80}, new int[]{10, 8, 6, 6}),
                        new SeedExercise(exercises.get("Overhead Press"), new double[]{35, 40, 40}, new int[]{10, 8, 8}),
                        new SeedExercise(exercises.get("Tricep Pushdown"), new double[]{25, 25, 30}, new int[]{12, 12, 10})
                )),
                new SessionTemplate("Pull deň", List.of(
                        new SeedExercise(exercises.get("Pull-up"), new double[]{0, 0, 5, 5}, new int[]{10, 8, 6, 5}),
                        new SeedExercise(exercises.get("Barbell Row"), new double[]{50, 60, 60, 65}, new int[]{10, 8, 8, 6}),
                        new SeedExercise(exercises.get("Bicep Curl"), new double[]{15, 17.5, 17.5}, new int[]{12, 10, 10})
                )),
                new SessionTemplate("Deň nôh", List.of(
                        new SeedExercise(exercises.get("Squat"), new double[]{60, 80, 100, 110, 110}, new int[]{10, 8, 5, 4, 4}),
                        new SeedExercise(exercises.get("Romanian Deadlift"), new double[]{60, 70, 80}, new int[]{10, 8, 8}),
                        new SeedExercise(exercises.get("Leg Press"), new double[]{120, 140, 160}, new int[]{12, 10, 8})
                )),
                new SessionTemplate("Push deň", List.of(
                        new SeedExercise(exercises.get("Bench Press"), new double[]{60, 65, 75}, new int[]{10, 8, 6}),
                        new SeedExercise(exercises.get("Incline Dumbbell Press"), new double[]{22.5, 25, 25}, new int[]{10, 8, 8})
                )),
                new SessionTemplate("Pull deň", List.of(
                        new SeedExercise(exercises.get("Pull-up"), new double[]{0, 0, 0}, new int[]{9, 7, 6}),
                        new SeedExercise(exercises.get("Barbell Row"), new double[]{50, 55, 60}, new int[]{10, 8, 8})
                )),
                new SessionTemplate("Deň nôh", List.of(
                        new SeedExercise(exercises.get("Squat"), new double[]{60, 80, 95}, new int[]{10, 8, 6}),
                        new SeedExercise(exercises.get("Leg Press"), new double[]{110, 130, 150}, new int[]{12, 10, 8})
                ))
        );

        int sessionsToCreate = TARGET_SESSION_COUNT - existingSessions;
        for (int i = 0; i < sessionsToCreate; i++) {
            SessionTemplate template = handPicked.get(i % handPicked.size());
            LocalDateTime startedAt = LocalDateTime.now()
                    .withHour(18).withMinute(0).withSecond(0).withNano(0)
                    .minusDays((long) (existingSessions + i) * 2);
            seedSession(user, template.name(), startedAt, template.exercises());
        }

        if (isNewUser) {
            seedWeightLog(user, 84.5, LocalDateTime.now().minusDays(14));
            seedWeightLog(user, 84.1, LocalDateTime.now().minusDays(10));
            seedWeightLog(user, 83.6, LocalDateTime.now().minusDays(7));
            seedWeightLog(user, 83.2, LocalDateTime.now().minusDays(3));
            seedWeightLog(user, 82.9, LocalDateTime.now());
        }
    }

    private Map<String, Exercise> ensureExercises() {
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

        Map<String, Exercise> exercises = new HashMap<>();
        for (Map.Entry<String, MuscleGroup> entry : definitions.entrySet()) {
            Exercise existing = exerciseRepository.findByName(entry.getKey());
            if (existing != null) {
                exercises.put(entry.getKey(), existing);
                continue;
            }
            exercises.put(entry.getKey(), exerciseRepository.save(exercise(entry.getKey(), entry.getValue())));
        }
        return exercises;
    }

    private Exercise exercise(String name, MuscleGroup muscleGroup) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setMuscleGroup(muscleGroup);
        return exercise;
    }

    private void seedSession(AppUser user, String name, LocalDateTime startedAt, List<SeedExercise> seedExercises) {
        WorkoutSession session = new WorkoutSession();
        session.setName(name);
        session.setStartedAt(startedAt);
        int totalSets = seedExercises.stream().mapToInt(se -> se.weights().length).sum();
        int durationMinutes = 20 + totalSets * 4;
        session.setDurationMinutes(durationMinutes);
        session.setEndedAt(startedAt.plusMinutes(durationMinutes));
        session.setAppUser(user);

        int order = 0;
        for (SeedExercise seedExercise : seedExercises) {
            SessionExercise sessionExercise = new SessionExercise();
            sessionExercise.setSession(session);
            sessionExercise.setExercise(seedExercise.exercise());
            sessionExercise.setOrderIndex(order++);

            for (int i = 0; i < seedExercise.weights().length; i++) {
                WorkoutSet set = new WorkoutSet();
                set.setSessionExercise(sessionExercise);
                set.setWeight(seedExercise.weights()[i]);
                set.setReps(seedExercise.reps()[i]);
                sessionExercise.getWorkoutSets().add(set);
            }
            session.getSessionExercises().add(sessionExercise);
        }
        workoutSessionRepository.save(session);
    }

    private void seedWeightLog(AppUser user, double weight, LocalDateTime loggedAt) {
        WeightLog log = new WeightLog();
        log.setUser(user);
        log.setWeight(weight);
        log.setLoggedAt(loggedAt);
        weightLogRepository.save(log);
    }

    private record SeedExercise(Exercise exercise, double[] weights, int[] reps) {
    }

    private record SessionTemplate(String name, List<SeedExercise> exercises) {
    }
}