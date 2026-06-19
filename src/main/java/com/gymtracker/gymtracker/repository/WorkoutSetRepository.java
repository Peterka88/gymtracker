package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
}
