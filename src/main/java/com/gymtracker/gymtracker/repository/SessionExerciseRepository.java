package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.SessionExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Long> {
    List<SessionExercise> findAllBySessionIdOrderByOrderIndexAsc(Long sessionId);

    Optional<SessionExercise> findByIdAndSessionAppUserId(Long id, Long appUserId);

    @Query("SELECT se.session.id, COUNT(se) FROM SessionExercise se WHERE se.session.id IN :sessionIds GROUP BY se.session.id")
    List<Object[]> countBySessionIds(@Param("sessionIds") List<Long> sessionIds);
}