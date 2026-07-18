package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.weightLog.WeightLogRequestDTO;
import com.gymtracker.gymtracker.dto.weightLog.WeightLogResponseDTO;
import com.gymtracker.gymtracker.entity.WeightLog;
import com.gymtracker.gymtracker.repository.WeightLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeightLogService {

    private final WeightLogRepository weightLogRepository;
    private final AppUserService appUserService;

    public WeightLogResponseDTO create(Long userId, WeightLogRequestDTO dto) {
        var user = appUserService.getAppUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        WeightLog log = new WeightLog();
        log.setUser(user);
        log.setWeight(dto.weight());
        log.setLoggedAt(LocalDateTime.now());
        return WeightLogResponseDTO.from(weightLogRepository.save(log));
    }

    public List<WeightLogResponseDTO> getLogsForUser(Long userId) {
        return weightLogRepository.findByUserIdOrderByLoggedAtDesc(userId)
                .stream()
                .map(WeightLogResponseDTO::from)
                .collect(Collectors.toList());
    }
}