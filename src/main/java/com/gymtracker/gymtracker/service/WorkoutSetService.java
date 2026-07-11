package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetDTO;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetPatchDTO;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import com.gymtracker.gymtracker.repository.WorkoutSetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutSetService {

    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutSessionService workoutSessionService;
    private final PersonalRecordsService personalRecordsService;
    private final AppUserService appUserService;

    public WorkoutSetService(WorkoutSetRepository workoutSetRepository,
                             WorkoutSessionService workoutSessionService,
                             PersonalRecordsService personalRecordsService,
                             AppUserService appUserService) {
        this.workoutSetRepository = workoutSetRepository;
        this.workoutSessionService = workoutSessionService;
        this.personalRecordsService = personalRecordsService;
        this.appUserService = appUserService;
    }

    public WorkoutSetResponse createWorkoutSet(Long userId, WorkoutSetDTO dto) {
        AppUser user = appUserService.getAppUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        WorkoutSet saved = workoutSetRepository.save(mapDtoToSet(userId, new WorkoutSet(), dto));
        boolean isPR = personalRecordsService.checkAndUpdate(saved, user);
        return WorkoutSetResponse.from(saved, isPR);
    }

    public WorkoutSetResponse getWorkoutSetById(Long id) {
        return WorkoutSetResponse.from(workoutSetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout set not found")));
    }

    public WorkoutSetResponse partialUpdateWorkoutSet(Long userId, Long id, WorkoutSetPatchDTO dto) {
        WorkoutSet set = workoutSetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout set not found"));
        if (dto.getSessionExerciseId() != null)
            set.setSessionExercise(workoutSessionService.getSessionExerciseById(userId, dto.getSessionExerciseId()));
        if (dto.getWeight() != null)
            set.setWeight(dto.getWeight());
        if (dto.getReps() != null)
            set.setReps(dto.getReps());
        return WorkoutSetResponse.from(workoutSetRepository.save(set));
    }

    public void deleteWorkoutSet(Long id) {
        workoutSetRepository.deleteById(id);
    }

    private WorkoutSet mapDtoToSet(Long userId, WorkoutSet set, WorkoutSetDTO dto) {
        set.setSessionExercise(workoutSessionService.getSessionExerciseById(userId, dto.getSessionExerciseId()));
        set.setWeight(dto.getWeight());
        set.setReps(dto.getReps());
        return set;
    }
}