package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.dto.PersonalRecordResponse;
import com.gymtracker.gymtracker.service.PersonalRecordsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal-records")
public class PersonalRecordsController {

    private final PersonalRecordsService personalRecordsService;

    public PersonalRecordsController(PersonalRecordsService personalRecordsService) {
        this.personalRecordsService = personalRecordsService;
    }

    @GetMapping
    public ResponseEntity<List<PersonalRecordResponse>> getAllPersonalRecords(
            @RequestParam(required = false) Long exerciseId) {
        if (exerciseId != null) {
            return ResponseEntity.ok(personalRecordsService.getPersonalRecordsByExercise(exerciseId));
        }
        return ResponseEntity.ok(personalRecordsService.getAllPersonalRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalRecordResponse> getPersonalRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(personalRecordsService.getPersonalRecordById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonalRecord(@PathVariable Long id) {
        personalRecordsService.deletePersonalRecord(id);
        return ResponseEntity.noContent().build();
    }
}