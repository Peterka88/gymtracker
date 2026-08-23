package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.SessionExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Long> {
    @Query("SELECT DISTINCT se FROM SessionExercise se LEFT JOIN FETCH se.workoutSets " +
           "WHERE se.session.id = :sessionId ORDER BY se.orderIndex ASC")
    List<SessionExercise> findAllBySessionIdWithSetsOrderByOrderIndexAsc(@Param("sessionId") Long sessionId);

    Optional<SessionExercise> findByIdAndSessionAppUserId(Long id, Long appUserId);

    void deleteByIdAndSessionAppUserId(Long id, Long sessionAppUserId);

    @Query("SELECT se.session.id, COUNT(se) FROM SessionExercise se WHERE se.session.id IN :sessionIds GROUP BY se.session.id")
    List<Object[]> countBySessionIds(@Param("sessionIds") List<Long> sessionIds);

    @Query(nativeQuery = true, value = """
        SELECT id
        FROM (
            SELECT session_exercises.id AS id,
                    ROW_NUMBER() OVER (
                        PARTITION BY session_exercises.exercise_id
                        ORDER BY workout_sessions.ended_at DESC NULLS LAST
                    ) as rn
                FROM session_exercises
                JOIN workout_sessions ON workout_sessions.id = session_exercises.session_id
                WHERE session_exercises.exercise_id IN (:exerciseIds) AND session_exercises.session_id != :currentSessionId
            ) ranked
        WHERE rn = 1
    """)
    List<Long> findLastSessionsByExerciseIds(@Param("exerciseIds") List<Long> exerciseIds, @Param("currentSessionId") Long currentSessionId);

    Integer countSessionExerciseBySessionId(Long sessionId);

    void deleteByExerciseId(Long exerciseId);
}