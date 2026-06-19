package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
}
