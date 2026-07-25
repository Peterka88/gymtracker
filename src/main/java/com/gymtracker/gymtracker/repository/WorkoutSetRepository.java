package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    interface LastPerformedProjection {
        Long getExerciseId();
        LocalDateTime getLastDate();
        Double getLastWeight();
    }

    @Query(nativeQuery = true,
            value = """
                    SELECT DISTINCT ON (session_exercises.exercise_id) 
                    session_exercises.exercise_id AS exerciseId,
                    workout_sessions.started_at AS lastDate,
                    weight AS lastWeight
                    FROM workout_sets 
                    JOIN session_exercises ON  session_exercises.id = workout_sets.session_exercise_id
                    JOIN workout_sessions ON workout_sessions.id = session_exercises.session_id
                    WHERE session_exercises.exercise_id IN (:exerciseIds)
                    ORDER BY session_exercises.exercise_id, workout_sessions.started_at DESC
                    """)
    List<LastPerformedProjection> findLastPerformedByExercise(@Param("exerciseIds") List<Long> exerciseIds);
}
