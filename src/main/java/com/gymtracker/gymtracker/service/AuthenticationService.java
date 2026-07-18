package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.auth.AuthenticationRequest;
import com.gymtracker.gymtracker.dto.auth.AuthenticationResponse;
import com.gymtracker.gymtracker.dto.auth.RegisterRequest;
import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.repository.AppUserRepository;
import com.gymtracker.gymtracker.security.AppUserPrincipal;
import com.gymtracker.gymtracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (isUsernameTaken(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        var user = AppUser.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(new AppUserPrincipal(user));
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(new AppUserPrincipal(user));
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private boolean isUsernameTaken(String username) {
        return repository.findByUsername(username).isPresent();
    }
}
