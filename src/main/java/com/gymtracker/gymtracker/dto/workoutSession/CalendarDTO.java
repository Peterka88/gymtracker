package com.gymtracker.gymtracker.dto.workoutSession;

import lombok.Data;
import java.util.List;

public record CalendarDTO(
        Integer month,
        Integer year,
        List<CalendarDayDTO> days
) {

    @Data
    public static class CalendarDayDTO {
        private Long workoutSessionId;
        private Integer dayOfMonth;
        private Boolean hasPr;

        public CalendarDayDTO(Long workoutSessionId, int dayOfMonth, boolean hasPr) {
            this.workoutSessionId = workoutSessionId;
            this.dayOfMonth = dayOfMonth;
            this.hasPr = hasPr;
        }

    }
}
