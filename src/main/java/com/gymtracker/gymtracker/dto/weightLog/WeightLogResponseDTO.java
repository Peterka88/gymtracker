package com.gymtracker.gymtracker.dto.weightLog;

import com.gymtracker.gymtracker.entity.WeightLog;

import java.time.LocalDateTime;

public record WeightLogResponseDTO(Long id, Long userId, Double weight, LocalDateTime loggedAt) {

    public static WeightLogResponseDTO from(WeightLog log) {
        return new WeightLogResponseDTO(log.getId(), log.getUser().getId(), log.getWeight(), log.getLoggedAt());
    }
}