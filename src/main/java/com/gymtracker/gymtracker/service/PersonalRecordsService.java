package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.PersonalRecordResponse;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.PersonalRecord;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import com.gymtracker.gymtracker.repository.PersonalRecordsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonalRecordsService {

    private final PersonalRecordsRepository personalRecordsRepository;

    public PersonalRecordsService(PersonalRecordsRepository personalRecordsRepository) {
        this.personalRecordsRepository = personalRecordsRepository;
    }

    public List<PersonalRecordResponse> getAllPersonalRecords() {
        return personalRecordsRepository.findAllByOrderByAchievedAtDesc().stream()
                .map(PersonalRecordResponse::from)
                .collect(Collectors.toList());
    }

    public PersonalRecordResponse getPersonalRecordById(Long id) {
        return PersonalRecordResponse.from(
                personalRecordsRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal record not found"))
        );
    }

    public List<PersonalRecordResponse> getPersonalRecordsByExercise(Long exerciseId) {
        return personalRecordsRepository.findByExerciseId(exerciseId)
                .map(pr -> List.of(PersonalRecordResponse.from(pr)))
                .orElse(List.of());
    }

    // Called automatically after each WorkoutSet save
    public boolean checkAndUpdate(WorkoutSet set) {
        if (set.getWeight() == null) return false;

        Exercise exercise = set.getExercise();
        Optional<PersonalRecord> existing = personalRecordsRepository.findByExerciseId(exercise.getId());

        if (existing.isEmpty() || set.getWeight() > existing.get().getWeight()) {
            PersonalRecord pr = existing.orElseGet(PersonalRecord::new);
            pr.setExercise(exercise);
            pr.setWeight(set.getWeight());
            pr.setReps(set.getReps());
            pr.setAchievedAt(LocalDate.now());
            personalRecordsRepository.save(pr);
            return true;
        }
        return false;
    }

    public java.util.Map<Long, Double> getPrWeightByExercise() {
        return personalRecordsRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(
                        pr -> pr.getExercise().getId(),
                        PersonalRecord::getWeight
                ));
    }

    public void deletePersonalRecord(Long id) {
        personalRecordsRepository.deleteById(id);
    }
}