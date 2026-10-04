package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.MuscleGroup;
import com.gymtracker.gymtracker.entity.SessionExercise;
import com.gymtracker.gymtracker.entity.WorkoutSession;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class WorkoutSetRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;
    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;
    @Autowired
    private WorkoutSetRepository workoutSetRepository;

    @Test
    void findWeightHistoryByExercise_returnsMostRecentSetsFirst() {
        AppUser user = appUserRepository.save(AppUser.builder().username("tester").build());
        Exercise benchPress = exerciseRepository.save(
                Exercise.builder().name("Bench Press").muscleGroup(MuscleGroup.CHEST).build());

        saveSessionWithSet(user, benchPress, LocalDateTime.of(2026, 7, 1, 10, 0), 60.0);
        saveSessionWithSet(user, benchPress, LocalDateTime.of(2026, 7, 10, 10, 0), 70.0);

        List<WorkoutSetRepository.ExerciseWeightHistoryProjection> result =
                workoutSetRepository.findWeightHistoryByExercise(List.of(benchPress.getId()));

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getExerciseId()).isEqualTo(benchPress.getId());
        assertThat(result.getFirst().getWeight()).isEqualTo(70.0);
        assertThat(result.getFirst().getLastDate()).isEqualTo(LocalDateTime.of(2026, 7, 10, 10, 0));
        assertThat(result.getLast().getWeight()).isEqualTo(60.0);
    }

    @Test
    void findWeightHistoryByExercise_returnsRowsPerExercise_whenMultipleExercisesRequested() {
        AppUser user = appUserRepository.save(AppUser.builder().username("tester2").build());
        Exercise benchPress = exerciseRepository.save(
                Exercise.builder().name("Bench Press").muscleGroup(MuscleGroup.CHEST).build());
        Exercise squat = exerciseRepository.save(
                Exercise.builder().name("Squat").muscleGroup(MuscleGroup.LEGS).build());

        saveSessionWithSet(user, benchPress, LocalDateTime.of(2026, 7, 1, 10, 0), 60.0);
        saveSessionWithSet(user, benchPress, LocalDateTime.of(2026, 7, 10, 10, 0), 70.0);
        saveSessionWithSet(user, squat, LocalDateTime.of(2026, 7, 2, 10, 0), 100.0);
        saveSessionWithSet(user, squat, LocalDateTime.of(2026, 7, 11, 10, 0), 110.0);

        List<WorkoutSetRepository.ExerciseWeightHistoryProjection> result = workoutSetRepository
                .findWeightHistoryByExercise(List.of(benchPress.getId(), squat.getId()));

        assertThat(result).hasSize(4);
        assertThat(result)
                .extracting(WorkoutSetRepository.ExerciseWeightHistoryProjection::getExerciseId)
                .containsExactlyInAnyOrder(benchPress.getId(), benchPress.getId(), squat.getId(), squat.getId());
    }

    @Test
    void findWeightHistoryByExercise_capsAt20MostRecentSets() {
        AppUser user = appUserRepository.save(AppUser.builder().username("tester3").build());
        Exercise benchPress = exerciseRepository.save(
                Exercise.builder().name("Bench Press").muscleGroup(MuscleGroup.CHEST).build());

        for (int i = 1; i <= 25; i++) {
            saveSessionWithSet(user, benchPress, LocalDateTime.of(2026, 1, i, 10, 0), 50.0 + i);
        }

        List<WorkoutSetRepository.ExerciseWeightHistoryProjection> result =
                workoutSetRepository.findWeightHistoryByExercise(List.of(benchPress.getId()));

        assertThat(result).hasSize(20);
        assertThat(result.getFirst().getWeight()).isEqualTo(75.0);
        assertThat(result.getLast().getWeight()).isEqualTo(56.0);
    }

    private void saveSessionWithSet(AppUser user, Exercise exercise, LocalDateTime startedAt, double weight) {
        WorkoutSession session = workoutSessionRepository.save(
                WorkoutSession.builder().name("Session").startedAt(startedAt).appUser(user).build());
        SessionExercise sessionExercise = sessionExerciseRepository.save(
                SessionExercise.builder().session(session).exercise(exercise).orderIndex(0).build());
        workoutSetRepository.save(
                WorkoutSet.builder().sessionExercise(sessionExercise).weight(weight).reps(5).build());
    }
}