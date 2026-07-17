package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.personalRecord.PersonalRecordResponse;
import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.entity.PersonalRecord;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import com.gymtracker.gymtracker.repository.PersonalRecordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalRecordsService {

    private final PersonalRecordsRepository personalRecordsRepository;

    public List<PersonalRecordResponse> getAllPersonalRecords(Long userId) {
        return personalRecordsRepository.findByAppUserIdOrderByAchievedAtDesc(userId).stream()
                .map(PersonalRecordResponse::from)
                .collect(Collectors.toList());
    }

    public List<PersonalRecordResponse> getBestPRsForUser(Long userId) {
        return personalRecordsRepository.findBestPerExerciseForUser(userId).stream()
                .map(PersonalRecordResponse::from)
                .collect(Collectors.toList());
    }

    public PersonalRecordResponse getPersonalRecordById(Long id) {
        return PersonalRecordResponse.from(
                personalRecordsRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal record not found"))
        );
    }

    public void deleteByWorkoutSetId(Long setId) {
        personalRecordsRepository.deleteByWorkoutSetId(setId);
    }

    public List<PersonalRecordResponse> getPersonalRecordsByExercise(Long exerciseId, Long userId) {
        return personalRecordsRepository.findByExerciseIdAndAppUserIdOrderByAchievedAtDesc(exerciseId, userId).stream()
                .map(PersonalRecordResponse::from)
                .collect(Collectors.toList());
    }

    // Called automatically after each WorkoutSet save — always inserts a new PR row if it's a new best
    // TODO: editing or deleting a WorkoutSet after the fact doesn't recompute PR history — a lowered/removed
    // PR can leave stale rows, and a set that should retroactively become a PR (e.g. it beats what's now the
    // corrected record) never gets picked up. Needs a recompute-on-write pass over that exercise+user's sets.
    public boolean checkAndUpdate(WorkoutSet set, AppUser user) {
        if (set.getWeight() == null) return false;

        Optional<PersonalRecord> currentBest = personalRecordsRepository
                .findTopByExerciseIdAndAppUserIdOrderByWorkoutSetWeightDesc(set.getSessionExercise().getExercise().getId(), user.getId());

        if (currentBest.isEmpty() || set.getWeight() > currentBest.get().getWorkoutSet().getWeight()) {
            PersonalRecord pr = new PersonalRecord();
            pr.setExercise(set.getSessionExercise().getExercise());
            pr.setWorkoutSet(set);
            pr.setAppUser(user);
            personalRecordsRepository.save(pr);
            return true;
        }
        return false;
    }

    public Set<Long> getPrWorkoutSetIds(Long userId, Long workoutSessionId) {
        return Set.copyOf(personalRecordsRepository.findWorkoutSetIdsByAppUserIdAndSessionId(userId, workoutSessionId));
    }

    public void deletePersonalRecord(Long id) {
        personalRecordsRepository.deleteById(id);
    }

    public Set<Long> getSessionIdsWithPr(Long userId) {
        return Set.copyOf(personalRecordsRepository.findSessionIdsWithPrForUser(userId));
    }
}