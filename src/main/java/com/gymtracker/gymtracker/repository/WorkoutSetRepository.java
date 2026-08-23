package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    interface ExerciseWeightHistoryProjection {
        Long getExerciseId();
        LocalDateTime getLastDate();
        Double getWeight();
    }

    interface ExerciseLastWeightRepsProjection {
        Long getExerciseId();
        Double getWeight();
        Integer getReps();
    }

    @Query(nativeQuery = true,
            value = """
                    SELECT exercise_id AS exerciseId, started_at AS lastDate, weight
                    FROM (
                        SELECT session_exercises.exercise_id AS exercise_id,
                               workout_sessions.started_at AS started_at,
                               workout_sets.weight AS weight,
                               ROW_NUMBER() OVER (
                                   PARTITION BY session_exercises.exercise_id
                                   ORDER BY workout_sessions.started_at DESC
                               ) AS rn
                        FROM workout_sets
                        JOIN session_exercises ON session_exercises.id = workout_sets.session_exercise_id
                        JOIN workout_sessions ON workout_sessions.id = session_exercises.session_id
                        WHERE session_exercises.exercise_id IN (:exerciseIds)
                    ) ranked
                    WHERE rn <= 20
                    ORDER BY exercise_id, rn
                    """)
    List<ExerciseWeightHistoryProjection> findWeightHistoryByExercise(@Param("exerciseIds") List<Long> exerciseIds);

    @Query(nativeQuery = true,
        value = """
                SELECT exercise_id AS exerciseId, weight, reps
                FROM (
                    SELECT session_exercises.exercise_id AS exercise_id,
                           workout_sets.weight AS weight,
                           workout_sets.reps AS reps,
                           ROW_NUMBER() OVER (
                               PARTITION BY session_exercises.exercise_id
                               ORDER BY workout_sets.weight DESC, workout_sets.reps DESC
                           ) AS rn
                    FROM workout_sets
                    JOIN session_exercises ON session_exercises.id = workout_sets.session_exercise_id
                    WHERE workout_sets.session_exercise_id IN (:exerciseSessionIds)
                ) ranked
                WHERE rn = 1
                """)
    List<ExerciseLastWeightRepsProjection> findLastWeightAndRepsByExercise(@Param("exerciseSessionIds") List<Long> exerciseSessionIds);

    @Query("""
        SELECT ws FROM WorkoutSet ws
        JOIN FETCH ws.sessionExercise se
        JOIN FETCH se.session
        WHERE ws.sessionExercise.exercise.id = :exerciseId AND ws.sessionExercise.session.appUser.id = :userId
        """)
    List<WorkoutSet> findAllForExerciseAndAppUser(@Param("exerciseId") Long exerciseId, @Param("userId") Long userId);

    @Query("""
    SELECT ws FROM WorkoutSet ws
    JOIN FETCH ws.sessionExercise se
    WHERE se.exercise.id = :exerciseId AND se.session.id IN :sessionIds
    """)
    List<WorkoutSet> findAllBySessionIds(@Param("exerciseId") Long exerciseId, @Param("sessionIds") List<Long> sessionIds);
}
