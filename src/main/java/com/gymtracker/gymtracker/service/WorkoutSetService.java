package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.WorkoutSetReqDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.responses.WorkoutCreateSetResDTO;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import com.gymtracker.gymtracker.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class WorkoutSetService {

    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutSessionService workoutSessionService;
    private final PersonalRecordsService personalRecordsService;
    private final AppUserService appUserService;

    @Transactional
    public WorkoutCreateSetResDTO createWorkoutSet(Long userId, Long exerciseSessionId, WorkoutSetReqDTO dto) {
        AppUser user = appUserService.getAppUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        WorkoutSet saved = workoutSetRepository.save(mapDtoToSet(userId, exerciseSessionId, dto));
        boolean isPR = personalRecordsService.checkAndUpdate(saved, user);
        return WorkoutCreateSetResDTO.from(saved, isPR);
    }

    @Transactional
    public WorkoutSetResponse updateWorkoutSet(Long userId, Long id, WorkoutSetReqDTO dto) {
        WorkoutSet set = workoutSetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout set not found"));

        set.setWeight(dto.weight());
        set.setReps(dto.reps());

        AppUser user = appUserService.getAppUserById(userId);
        personalRecordsService.deleteByWorkoutSetId(id);

        boolean isPR = personalRecordsService.checkAndUpdate(set, user);
        return WorkoutSetResponse.from(workoutSetRepository.save(set), isPR);
    }

    public void deleteWorkoutSet(Long id) {
        workoutSetRepository.deleteById(id);
    }

    private WorkoutSet mapDtoToSet(Long userId, Long exerciseSessionId, WorkoutSetReqDTO dto) {
        WorkoutSet set = new WorkoutSet();
        set.setSessionExercise(workoutSessionService.getSessionExerciseById(userId, exerciseSessionId));
        set.setWeight(dto.weight());
        set.setReps(dto.reps());
        return set;
    }
}