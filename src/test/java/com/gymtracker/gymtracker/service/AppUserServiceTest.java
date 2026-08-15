package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserService appUserService;

    @Test
    void getAppUserById_returnsUser_whenFound() {
        AppUser user = AppUser.builder().id(1L).name("Martin").username("martin").build();
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));

        AppUser result = appUserService.getAppUserById(1L);

        assertThat(result.getUsername()).isEqualTo("martin");
    }

    @Test
    void getAppUserById_throwsNotFound_whenMissing() {
        when(appUserRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appUserService.getAppUserById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}