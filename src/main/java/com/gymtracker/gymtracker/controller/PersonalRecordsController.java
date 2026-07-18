package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.personalRecord.PersonalRecordResponse;
import com.gymtracker.gymtracker.security.AppUserPrincipal;
import com.gymtracker.gymtracker.service.PersonalRecordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Personal Records", description = "View best lifts per exercise, updated automatically")
@RestController
@RequestMapping("/api/personal-records")
@RequiredArgsConstructor
public class PersonalRecordsController {

    private final PersonalRecordsService personalRecordsService;

    @Operation(summary = "Get PR history for a user", description = "Full history ordered by date. Optionally filter by ?exerciseId=")
    @ApiResponse(responseCode = "200", description = "List of personal records")
    @GetMapping
    public ResponseEntity<List<PersonalRecordResponse>> getAllPersonalRecords(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Parameter(description = "Filter by exercise ID") @RequestParam(required = false) Long exerciseId) {
        if (exerciseId != null) {
            return ResponseEntity.ok(personalRecordsService.getPersonalRecordsByExercise(exerciseId, principal.getId()));
        }
        return ResponseEntity.ok(personalRecordsService.getBestPRsForUser(principal.getId()));
    }

    @Operation(summary = "Get a personal record by ID")
    @ApiResponse(responseCode = "200", description = "Personal record found")
    @ApiResponse(responseCode = "404", description = "Personal record not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Personal record not found",
                              "instance": "/api/personal-records/99"
                            }"""
            )))
    @GetMapping("/{id}")
    public ResponseEntity<PersonalRecordResponse> getPersonalRecordById(
            @Parameter(description = "Personal record ID") @PathVariable Long id) {
        return ResponseEntity.ok(personalRecordsService.getPersonalRecordById(id));
    }

    @Operation(summary = "Delete a personal record by ID")
    @ApiResponse(responseCode = "204", description = "Personal record deleted")
    @ApiResponse(responseCode = "404", description = "Personal record not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    value = """
                            {
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Personal record not found",
                              "instance": "/api/personal-records/99"
                            }"""
            )))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonalRecord(
            @Parameter(description = "Personal record ID") @PathVariable Long id) {
        personalRecordsService.deletePersonalRecord(id);
        return ResponseEntity.noContent().build();
    }
}