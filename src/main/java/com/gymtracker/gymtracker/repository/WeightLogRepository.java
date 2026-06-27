package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {
    List<WeightLog> findByAppUserIdOrderByLoggedAtDesc(Long userId);
}