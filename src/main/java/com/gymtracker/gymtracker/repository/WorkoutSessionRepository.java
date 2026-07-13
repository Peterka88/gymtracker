package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.WorkoutSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findAllByAppUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);

    Optional<WorkoutSession> findByAppUserIdAndId(Long appUserId, Long id);

    Optional<WorkoutSession> findByAppUserIdAndEndedAtIsNull(Long appUserId);

    void deleteByAppUserIdAndId(Long appUser_id, Long id);
}
