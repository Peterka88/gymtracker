package com.gymtracker.gymtracker.dto.weightLog;

import com.gymtracker.gymtracker.entity.WeightLog;

import java.time.LocalDateTime;

public class WeightLogResponseDTO {

    private Long id;

    private Long userId;

    private Double weight;

    private LocalDateTime loggedAt;


    public static WeightLogResponseDTO from(WeightLog log) {
        return new WeightLogResponseDTO(
                log.getId(),
                log.getUser().getId(),
                log.getWeight(),
                log.getLoggedAt()
        );
    }

    public WeightLogResponseDTO(Long id, Long userId, Double weight, LocalDateTime loggedAt) {
        this.id = id;
        this.userId = userId;
        this.weight = weight;
        this.loggedAt = loggedAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Double getWeight() { return weight; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
}
