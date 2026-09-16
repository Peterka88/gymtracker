package com.gymtracker.gymtracker.dto.newWorkoutSession.requests;

public record LocationPatchReqDTO(
        String locationName,
        String address,
        Double latitude,
        Double longitude
        ) {
}
