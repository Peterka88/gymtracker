package com.gymtracker.gymtracker.dto.appUser;

import com.gymtracker.gymtracker.entity.AppUser;

public class AppUserResponseDTO {

    private Long id;
    private String username;
    private String name;
    private Double height;

    public static AppUserResponseDTO from(AppUser user) {
        return new AppUserResponseDTO(user.getId(), user.getUsername(), user.getName(), user.getHeight());
    }

    public AppUserResponseDTO(Long id, String username, String name, Double height) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.height = height;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public Double getHeight() { return height; }
}