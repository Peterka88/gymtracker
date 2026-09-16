package com.gymtracker.gymtracker.dto.workoutSession;

import com.gymtracker.gymtracker.entity.Location;

public record LocationDTO(
        String locationName,
        String address,
        Double latitude,
        Double longitude
) {

    public static LocationDTO from(Location location) {
        return new LocationDTO(
                location.getLocationName(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude()
        );
    }
}
