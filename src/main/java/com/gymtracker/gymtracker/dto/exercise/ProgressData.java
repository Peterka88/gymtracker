package com.gymtracker.gymtracker.dto.exercise;

import java.time.LocalDate;

public record ProgressData(
        LocalDate date,
        Double weight,
        Double volume,
        Double estimated1RM
) {
    public static ProgressData create(LocalDate date, Double weight, Double volume, Double estimated1RM) {
        return new ProgressData(date, weight, volume, estimated1RM);
    }
}
