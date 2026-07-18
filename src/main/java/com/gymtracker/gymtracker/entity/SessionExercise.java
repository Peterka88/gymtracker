package com.gymtracker.gymtracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session_exercises")
public class SessionExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sessionId", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private WorkoutSession session;

    @ManyToOne
    @JoinColumn(name = "exerciseId", nullable = false)
    private Exercise exercise;

    private Integer orderIndex;

    private String note;

    @OneToMany(mappedBy = "sessionExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkoutSet> workoutSets = new ArrayList<>();

}