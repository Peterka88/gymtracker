package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.weightLog.WeightLogRequestDTO;
import com.gymtracker.gymtracker.dto.weightLog.WeightLogResponseDTO;
import com.gymtracker.gymtracker.security.AppUserPrincipal;
import com.gymtracker.gymtracker.service.WeightLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Weight Logs", description = "Track body weight over time")
@RestController
@RequestMapping("/api/weight-logs")
@RequiredArgsConstructor
public class WeightLogController {

    private final WeightLogService weightLogService;

    @Operation(summary = "Log a new body weight entry")
    @ApiResponse(responseCode = "201", description = "Weight log created successfully")
    @PostMapping
    public ResponseEntity<WeightLogResponseDTO> createWeightLog(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @RequestBody @Valid WeightLogRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(weightLogService.create(principal.getId(), dto));
    }

    @Operation(summary = "Get all weight logs for a user")
    @GetMapping
    public ResponseEntity<List<WeightLogResponseDTO>> getWeightLogs(
            @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(weightLogService.getLogsForUser(principal.getId()));
    }
}