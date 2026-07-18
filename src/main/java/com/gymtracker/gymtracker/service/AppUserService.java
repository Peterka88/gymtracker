package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUser getAppUserById(Long id) {
        return appUserRepository.findById(id).orElse(null);
    }

}
