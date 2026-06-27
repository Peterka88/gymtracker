package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.personalRecord.PersonalRecordResponse;
import com.gymtracker.gymtracker.entity.PersonalRecord;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import com.gymtracker.gymtracker.repository.PersonalRecordsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
        return personalRecordsRepository.findByExerciseIdOrderByAchievedAtDesc(exerciseId).stream()
                .map(PersonalRecordResponse::from)
                .collect(Collectors.toList());
    }

    // Called automatically after each WorkoutSet save — always inserts a new PR row if it's a new best
    public boolean checkAndUpdate(WorkoutSet set) {
        if (set.getWeight() == null) return false;

        Optional<PersonalRecord> currentBest = personalRecordsRepository
                .findTopByExerciseIdOrderByWeightDesc(set.getExercise().getId());

        if (currentBest.isEmpty() || set.getWeight() > currentBest.get().getWeight()) {
            PersonalRecord pr = new PersonalRecord();
            pr.setExercise(set.getExercise());
            pr.setWorkoutSet(set);
            pr.setWeight(set.getWeight());
            pr.setReps(set.getReps());
            pr.setAchievedAt(LocalDate.now());
            personalRecordsRepository.save(pr);
            return true;
        }
        return false;
    }

    public Map<Long, Double> getPrWeightByExercise() {
        return personalRecordsRepository.findAll().stream()
                .collect(Collectors.toMap(
                        pr -> pr.getExercise().getId(),
                        PersonalRecord::getWeight,
                        Math::max
                ));
    }

    public void deletePersonalRecord(Long id) {
        personalRecordsRepository.deleteById(id);
    }
}