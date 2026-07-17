package com.gymtracker.gymtracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "personal_records")
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exerciseId")
    private Exercise exercise;

    @OneToOne
    @JoinColumn(name = "workoutSetId", unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private WorkoutSet workoutSet;

    @ManyToOne
    @JoinColumn(name = "userId")
    private AppUser appUser;

}