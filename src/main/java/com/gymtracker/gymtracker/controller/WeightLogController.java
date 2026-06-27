package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.weightLog.WeightLogRequestDTO;
import com.gymtracker.gymtracker.dto.weightLog.WeightLogResponseDTO;
import com.gymtracker.gymtracker.service.WeightLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: replace @RequestParam Long userId with @AuthenticationPrincipal once JWT is implemented
@Tag(name = "Weight Logs", description = "Track body weight over time")
@RestController
@RequestMapping("/api/weight-logs")
public class WeightLogController {

    private final WeightLogService weightLogService;

    public WeightLogController(WeightLogService weightLogService) {
        this.weightLogService = weightLogService;
    }

    @Operation(summary = "Log a new body weight entry")
    @ApiResponse(responseCode = "201", description = "Weight log created successfully")
    @PostMapping
    public ResponseEntity<WeightLogResponseDTO> createWeightLog(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @RequestBody @Valid WeightLogRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(weightLogService.create(userId, dto));
    }

    @Operation(summary = "Get all weight logs for a user")
    @GetMapping
    public ResponseEntity<List<WeightLogResponseDTO>> getWeightLogs(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        return ResponseEntity.ok(weightLogService.getLogsForUser(userId));
    }
}