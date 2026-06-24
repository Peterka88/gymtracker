package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.appUser.AppUserRequestDTO;
import com.gymtracker.gymtracker.dto.appUser.AppUserResponseDTO;
import com.gymtracker.gymtracker.repository.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }


    public AppUserResponseDTO createUser(AppUserRequestDTO dto) {
        if (isUsernameTaken(dto.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        return AppUserResponseDTO.from(appUserRepository.save(dto.toEntity()));
    }

    private boolean isUsernameTaken(String username) {
        return appUserRepository.findByUsername(username) != null;
    }
}
