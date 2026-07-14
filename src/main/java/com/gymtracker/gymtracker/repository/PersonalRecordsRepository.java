package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.PersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonalRecordsRepository extends JpaRepository<PersonalRecord, Long> {
    @Query("SELECT pr FROM PersonalRecord pr WHERE pr.appUser.id = :userId " +
           "ORDER BY pr.workoutSet.sessionExercise.session.startedAt DESC")
    List<PersonalRecord> findByAppUserIdOrderByAchievedAtDesc(@Param("userId") Long userId);

    @Query("SELECT pr FROM PersonalRecord pr WHERE pr.exercise.id = :exerciseId AND pr.appUser.id = :userId " +
           "ORDER BY pr.workoutSet.sessionExercise.session.startedAt DESC")
    List<PersonalRecord> findByExerciseIdAndAppUserIdOrderByAchievedAtDesc(@Param("exerciseId") Long exerciseId, @Param("userId") Long userId);

    Optional<PersonalRecord> findTopByExerciseIdAndAppUserIdOrderByWorkoutSetWeightDesc(Long exerciseId, Long userId);

    @Query("SELECT pr FROM PersonalRecord pr WHERE pr.appUser.id = :userId AND pr.workoutSet.weight = " +
           "(SELECT MAX(pr2.workoutSet.weight) FROM PersonalRecord pr2 WHERE pr2.exercise = pr.exercise AND pr2.appUser.id = :userId)")
    List<PersonalRecord> findBestPerExerciseForUser(@Param("userId") Long userId);

    @Query("SELECT DISTINCT pr.workoutSet.sessionExercise.session.id FROM PersonalRecord pr " +
           "WHERE pr.appUser.id = :userId")
    List<Long> findSessionIdsWithPrForUser(@Param("userId") Long userId);

    @Query("SELECT pr.workoutSet.id FROM PersonalRecord pr " +
            "WHERE pr.appUser.id = :userId AND pr.workoutSet.sessionExercise.session.id = :workoutSessionId")
    List<Long> findWorkoutSetIdsByAppUserIdAndSessionId(@Param("userId") Long userId, @Param("workoutSessionId") Long workoutSessionId);

    void deleteByWorkoutSetId(Long setId);
}