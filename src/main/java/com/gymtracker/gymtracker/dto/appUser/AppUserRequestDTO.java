package com.gymtracker.gymtracker.dto.appUser;

import com.gymtracker.gymtracker.entity.AppUser;

public class AppUserRequestDTO {

    private String username;
    private String password;
    private String name;
    private Double height;

    public AppUserRequestDTO(String username, String password, String name, Double height) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.height = height;
    }

    public AppUser toEntity() {
        AppUser user = new AppUser();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setName(this.name);
        user.setHeight(this.height);
        return user;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Double getHeight() {
        return height;
    }
}
