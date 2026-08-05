package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.WorkoutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findAllByAppUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);

    Optional<WorkoutSession> findByAppUserIdAndId(Long appUserId, Long id);

    Optional<WorkoutSession> findByAppUserIdAndEndedAtIsNull(Long appUserId);

    void deleteByAppUserIdAndId(Long appUser_id, Long id);

    @Query("""
        SELECT DISTINCT s FROM WorkoutSession s
        JOIN s.sessionExercises se
        WHERE se.exercise.id = :exerciseId AND s.appUser.id = :userId
        ORDER BY s.startedAt DESC
        """)
    Page<WorkoutSession> findSessionsForExercise(@Param("exerciseId") Long exerciseId, @Param("userId") Long userId, Pageable pageable);
}
