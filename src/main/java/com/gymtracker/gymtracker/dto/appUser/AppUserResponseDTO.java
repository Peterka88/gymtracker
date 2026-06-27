package com.gymtracker.gymtracker.dto.appUser;

import com.gymtracker.gymtracker.entity.AppUser;

public record AppUserResponseDTO(Long id, String username, String name, Double height) {

    public static AppUserResponseDTO from(AppUser user) {
        return new AppUserResponseDTO(user.getId(), user.getUsername(), user.getName(), user.getHeight());
    }

}