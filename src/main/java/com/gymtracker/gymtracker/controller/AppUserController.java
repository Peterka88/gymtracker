package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.appUser.AppUserRequestDTO;
import com.gymtracker.gymtracker.dto.appUser.AppUserResponseDTO;
import com.gymtracker.gymtracker.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "Manage Users and their authentication")
@RestController
@RequestMapping("/api/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Operation(summary = "Create a new user", description = "Registers a new user with the provided details")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @PostMapping
    public AppUserResponseDTO createUser(@RequestBody @Valid AppUserRequestDTO dto) {
        return appUserService.createUser(dto);
    }

}
