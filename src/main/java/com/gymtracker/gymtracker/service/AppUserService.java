package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUser getAppUserById(Long id) {
        return appUserRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

}
