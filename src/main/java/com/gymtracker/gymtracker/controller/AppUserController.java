package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.appUser.AppUserRequestDTO;
import com.gymtracker.gymtracker.dto.appUser.AppUserResponseDTO;
import com.gymtracker.gymtracker.service.AppUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping
    public AppUserResponseDTO createUser(AppUserRequestDTO dto) {
        return appUserService.createUser(dto);
    }

}
