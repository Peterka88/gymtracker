package com.gymtracker.gymtracker.dto.createNewWorkoutSession.responses;

import com.gymtracker.gymtracker.entity.WorkoutSet;

public record WorkoutCreateSetResDTO (
        Long id,
        Double weight,
        Integer reps,
        Boolean pr
) {

    public static WorkoutCreateSetResDTO from(WorkoutSet set, boolean pr) {
        return new WorkoutCreateSetResDTO(set.getId(), set.getWeight(), set.getReps(), pr);
    }

}
