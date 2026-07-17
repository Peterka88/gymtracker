package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.appUser.AppUserRequestDTO;
import com.gymtracker.gymtracker.dto.appUser.AppUserResponseDTO;
import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserResponseDTO createUser(AppUserRequestDTO dto) {
        if (isUsernameTaken(dto.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        return AppUserResponseDTO.from(appUserRepository.save(dto.toEntity()));
    }

    public AppUser getAppUserById(Long id) {
        return appUserRepository.findById(id).orElse(null);
    }

    private boolean isUsernameTaken(String username) {
        return appUserRepository.findByUsername(username) != null;
    }
}
