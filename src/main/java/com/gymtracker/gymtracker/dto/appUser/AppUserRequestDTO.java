package com.gymtracker.gymtracker.dto.appUser;

import com.gymtracker.gymtracker.entity.AppUser;
import jakarta.validation.constraints.NotBlank;

public record AppUserRequestDTO(@NotBlank(message = "Username cannot be blank") String username,
                                @NotBlank(message = "Password cannot be blank") String password,
                                @NotBlank(message = "Name cannot be blank") String name, Double height) {

    public AppUser toEntity() {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setHeight(height);
        return user;
    }
}