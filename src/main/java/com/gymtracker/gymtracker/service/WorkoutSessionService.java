package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDetailResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionRequestDTO;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionResponse;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.WorkoutSession;
import com.gymtracker.gymtracker.repository.WorkoutSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutSessionService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final AppUserService appUserService;

    public WorkoutSessionService(WorkoutSessionRepository workoutSessionRepository, AppUserService appUserService) {
        this.workoutSessionRepository = workoutSessionRepository;
        this.appUserService = appUserService;
    }

    public List<WorkoutSessionResponse> getAllWorkoutSessions(Long userId) {
        return workoutSessionRepository.findAllByAppUserId(userId).stream()
                .map(WorkoutSessionResponse::from)
                .collect(Collectors.toList());
    }

    public WorkoutSession getWorkoutSessionById(Long userId, Long id) {
        return workoutSessionRepository.findByAppUserIdAndId(userId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout session not found"));
    }

    public WorkoutSessionDetailResponse getWorkoutSessionDetail(Long userId, Long id) {
        WorkoutSession session = getWorkoutSessionById(userId, id);
        List<WorkoutSetResponse> sets = session.getWorkoutSets().stream()
                .map(WorkoutSetResponse::from)
                .collect(Collectors.toList());
        return new WorkoutSessionDetailResponse(session.getId(), session.getDate(), session.getNote(), sets);
    }

    public WorkoutSessionResponse createWorkoutSession(Long userId, WorkoutSessionRequestDTO dto) {
        var appUser = appUserService.getAppUserById(userId);

        WorkoutSession session = new WorkoutSession();
        session.setName(dto.getName() == null ? dto.getDate().toString() : dto.getName());
        session.setDate(dto.getDate());
        session.setNote(dto.getNote());
        session.setAppUser(appUser);
        return WorkoutSessionResponse.from(workoutSessionRepository.save(session));
    }

    public void deleteWorkoutSession(Long userId, Long id) {
        workoutSessionRepository.deleteAllByAppUserIdAndId(userId, id);
    }

    public List<WorkoutSetResponse> getWorkoutSetsBySessionId(Long userId, Long id) {
        return getWorkoutSessionById(userId, id).getWorkoutSets().stream()
                .map(WorkoutSetResponse::from)
                .collect(Collectors.toList());
    }
}
