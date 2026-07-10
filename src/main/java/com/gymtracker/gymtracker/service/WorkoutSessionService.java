package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.sessionExercise.SessionExerciseResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDetailResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionRequestDTO;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionResponse;
import com.gymtracker.gymtracker.dto.workoutSet.WorkoutSetResponse;
import com.gymtracker.gymtracker.entity.WorkoutSession;
import com.gymtracker.gymtracker.repository.SessionExerciseRepository;
import com.gymtracker.gymtracker.repository.WorkoutSessionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
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

    public WorkoutSessionService(WorkoutSessionRepository workoutSessionRepository,
                                  SessionExerciseRepository sessionExerciseRepository,
                                  AppUserService appUserService,
                                  PersonalRecordsService personalRecordsService) {
        this.workoutSessionRepository = workoutSessionRepository;
        this.sessionExerciseRepository = sessionExerciseRepository;
        this.appUserService = appUserService;
        this.personalRecordsService = personalRecordsService;
    }

    public List<WorkoutSessionResponse> getWorkoutSessions(Long userId, Integer paramSize, Integer page) {
        int size = (paramSize == null || paramSize == 0) ? DEFAULT_PAGE_SIZE : paramSize;
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size);
        Set<Long> sessionIdsWithPr = personalRecordsService.getSessionIdsWithPr(userId);

        List<WorkoutSession> sessions = workoutSessionRepository.findAllByAppUserIdOrderByDateDesc(userId, pageable);
        List<Long> sessionIds = sessions.stream().map(WorkoutSession::getId).collect(Collectors.toList());
        Map<Long, Integer> exerciseCounts = sessionIds.isEmpty() ? Map.of() : sessionExerciseRepository.countBySessionIds(sessionIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Long) row[1]).intValue()));

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
        List<SessionExerciseResponse> exercises = session.getSessionExercises().stream()
                .sorted(Comparator.comparing(se -> se.getOrderIndex() == null ? Integer.MAX_VALUE : se.getOrderIndex()))
                .map(SessionExerciseResponse::from)
                .collect(Collectors.toList());
        return new WorkoutSessionDetailResponse(session.getId(), session.getDate(), session.getNote(), exercises);
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
        return getWorkoutSessionById(userId, id).getSessionExercises().stream()
                .flatMap(sessionExercise -> sessionExercise.getWorkoutSets().stream())
                .map(WorkoutSetResponse::from)
                .collect(Collectors.toList());
    }
}