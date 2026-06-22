package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.PersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonalRecordsRepository extends JpaRepository<PersonalRecord, Long> {
    Optional<PersonalRecord> findByExerciseId(Long exerciseId);
    List<PersonalRecord> findAllByOrderByAchievedAtDesc();
}
