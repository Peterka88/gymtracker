package com.gymtracker.gymtracker.repository;

import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.MuscleGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByName(String name);

    @Query("""
        SELECT e FROM Exercise e
        WHERE (:search IS NULL OR LOWER(e.name) LIKE :search)
              AND (:muscleGroups IS NULL OR e.muscleGroup IN :muscleGroups)
        """)
    Page<Exercise> search(@Param("search") String name, @Param("muscleGroups") List<MuscleGroup> muscleGroupList, Pageable pageable);
}
