package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.sessionExercise.SessionExerciseDTO;
import com.gymtracker.gymtracker.dto.sessionExercise.SessionExerciseResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDetailResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionRequestDTO;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionResponse;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.SessionExercise;
import com.gymtracker.gymtracker.entity.WorkoutSession;
import com.gymtracker.gymtracker.repository.SessionExerciseRepository;
import com.gymtracker.gymtracker.repository.WorkoutSessionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkoutSessionService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final WorkoutSessionRepository workoutSessionRepository;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final AppUserService appUserService;
    private final PersonalRecordsService personalRecordsService;
    private final ExerciseService exerciseService;
    public WorkoutSessionService(WorkoutSessionRepository workoutSessionRepository,
                                  SessionExerciseRepository sessionExerciseRepository,
                                  AppUserService appUserService,
                                  PersonalRecordsService personalRecordsService,
                                  ExerciseService exerciseService) {
        this.workoutSessionRepository = workoutSessionRepository;
        this.sessionExerciseRepository = sessionExerciseRepository;
        this.appUserService = appUserService;
        this.personalRecordsService = personalRecordsService;
        this.exerciseService = exerciseService;
    }

    public List<WorkoutSessionResponse> getWorkoutSessions(Long userId, Integer paramSize, Integer page) {
        int size = (paramSize == null || paramSize == 0) ? DEFAULT_PAGE_SIZE : paramSize;
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size);
        Set<Long> sessionIdsWithPr = personalRecordsService.getSessionIdsWithPr(userId);

        List<WorkoutSession> sessions = workoutSessionRepository.findAllByAppUserIdOrderByStartedAtDesc(userId, pageable);
        List<Long> sessionIds = sessions.stream().map(WorkoutSession::getId).collect(Collectors.toList());
        Map<Long, Integer> exerciseCounts = countExercisesBySessionIds(sessionIds);

        return sessions.stream()
                .map(session -> WorkoutSessionResponse.from(
                        session,
                        exerciseCounts.getOrDefault(session.getId(), 0),
                        sessionIdsWithPr.contains(session.getId())))
                .collect(Collectors.toList());
    }

    public WorkoutSession getWorkoutSessionById(Long userId, Long id) {
        return workoutSessionRepository.findByAppUserIdAndId(userId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout session not found"));
    }

    public WorkoutSessionDetailResponse getWorkoutSessionDetail(Long userId, Long id) {
        WorkoutSession session = getWorkoutSessionById(userId, id);
        List<SessionExercise> sessionExerciseList = sessionExerciseRepository.findAllBySessionIdWithSetsOrderByOrderIndexAsc(id);
        Set<Long> prWorkoutSetIds = personalRecordsService.getPrWorkoutSetIds(userId, id);

        return new WorkoutSessionDetailResponse(
                session.getId(),
                session.getName(),
                session.getStartedAt(),
                session.getDurationMinutes(),
                session.getNote(),
                !prWorkoutSetIds.isEmpty(),
                sessionExerciseList.stream()
                        .map(se -> SessionExerciseResponse.from(se, prWorkoutSetIds))
                        .collect(Collectors.toList())
        );
    }

    public WorkoutSessionResponse createWorkoutSession(Long userId, WorkoutSessionRequestDTO dto) {
        var appUser = appUserService.getAppUserById(userId);

        WorkoutSession session = new WorkoutSession();
        session.setName(dto.getName() == null ? dto.getStartedAt().toString() : dto.getName());
        session.setStartedAt(dto.getStartedAt());
        session.setDurationMinutes(dto.getDurationMinutes());
        session.setNote(dto.getNote());
        session.setAppUser(appUser);
        return WorkoutSessionResponse.from(workoutSessionRepository.save(session));
    }

    public void deleteWorkoutSession(Long userId, Long id) {
        workoutSessionRepository.deleteAllByAppUserIdAndId(userId, id);
    }

    public List<WorkoutSetResponse> getWorkoutSetsBySessionId(Long userId, Long id) {
        Set<Long> prWorkoutSetIds = personalRecordsService.getPrWorkoutSetIds(userId, id);
        return getWorkoutSessionById(userId, id).getSessionExercises().stream()
                .flatMap(sessionExercise -> sessionExercise.getWorkoutSets().stream())
                .map(set -> WorkoutSetResponse.from(set, prWorkoutSetIds))
                .collect(Collectors.toList());
    }

    public SessionExerciseResponse createSessionExercise(Long userId, Long sessionId, SessionExerciseDTO dto) {
        WorkoutSession session = getWorkoutSessionById(userId, sessionId);

        SessionExercise sessionExercise = new SessionExercise();
        sessionExercise.setSession(session);
        sessionExercise.setExercise(exerciseService.getExerciseById(dto.getExerciseId()));
        sessionExercise.setOrderIndex(dto.getOrderIndex());
        return SessionExerciseResponse.from(sessionExerciseRepository.save(sessionExercise));
    }

    public List<SessionExerciseResponse> getSessionExercises(Long userId, Long sessionId) {
        Set<Long> prWorkoutSetIds = personalRecordsService.getPrWorkoutSetIds(userId, sessionId);
        return sessionExerciseRepository.findAllBySessionIdWithSetsOrderByOrderIndexAsc(sessionId).stream()
                .map(se -> SessionExerciseResponse.from(se, prWorkoutSetIds))
                .collect(Collectors.toList());
    }

    public SessionExercise getSessionExerciseById(Long userId, Long id) {
        return sessionExerciseRepository.findByIdAndSessionAppUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session exercise not found"));
    }

    private Map<Long, Integer> countExercisesBySessionIds(List<Long> sessionIds) {
        if (sessionIds.isEmpty()) {
            return Map.of();
        }
        return sessionExerciseRepository.countBySessionIds(sessionIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Long) row[1]).intValue()));
    }
}