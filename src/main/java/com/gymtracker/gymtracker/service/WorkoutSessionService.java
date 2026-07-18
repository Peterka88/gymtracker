package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.SessionExerciseCreateDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.SessionExerciseNoteDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.WorkoutSessionPatchDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.responses.*;
import com.gymtracker.gymtracker.dto.sessionExercise.SessionExerciseResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDetailResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionResponse;
import com.gymtracker.gymtracker.entity.SessionExercise;
import com.gymtracker.gymtracker.entity.WorkoutSession;
import com.gymtracker.gymtracker.repository.SessionExerciseRepository;
import com.gymtracker.gymtracker.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final WorkoutSessionRepository workoutSessionRepository;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final AppUserService appUserService;
    private final PersonalRecordsService personalRecordsService;
    private final ExerciseService exerciseService;

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
                session.getEndedAt(),
                session.getDurationMinutes(),
                session.getNote(),
                !prWorkoutSetIds.isEmpty(),
                sessionExerciseList.stream()
                        .map(se -> SessionExerciseResponse.from(se, prWorkoutSetIds))
                        .collect(Collectors.toList())
        );
    }

    public WorkoutSessionStartResult createWorkoutSession(Long userId) {
        var activeSession = workoutSessionRepository.findByAppUserIdAndEndedAtIsNull(userId);
        if (activeSession.isPresent()) {
            return new WorkoutSessionStartResult(WorkoutSessionStartResDTO.from(activeSession.get()), false);
        }

        var appUser = appUserService.getAppUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        WorkoutSession session = new WorkoutSession();
        session.setName(now.toLocalDate().toString());
        session.setStartedAt(now);
        session.setAppUser(appUser);
        WorkoutSession saved = workoutSessionRepository.save(session);
        return new WorkoutSessionStartResult(WorkoutSessionStartResDTO.from(saved), true);
    }

    public WorkoutSessionPatchResDTO updateWorkoutSessionNameOrNote(Long id, Long userId, WorkoutSessionPatchDTO dto) {
        WorkoutSession session = workoutSessionRepository.findByAppUserIdAndId(userId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Session not found"));

        if (dto.name() != null)
            session.setName(dto.name());
        if (dto.note() != null)
            session.setNote(dto.note());

        return WorkoutSessionPatchResDTO.from(workoutSessionRepository.save(session));
    }

    public WorkoutSessionFinishResDTO finishWorkoutSession(Long userId, Long id) {
        WorkoutSession session = getWorkoutSessionById(userId, id);
        if (session.getEndedAt() != null) {
            throw new IllegalArgumentException("Workout session already finished");
        }

        session.setEndedAt(LocalDateTime.now());
        session.setDurationMinutes((int) Duration.between(session.getStartedAt(), session.getEndedAt()).toMinutes());
        return WorkoutSessionFinishResDTO.from(workoutSessionRepository.save(session));
    }

    @Transactional
    public void deleteWorkoutSession(Long userId, Long sessionId) {
        workoutSessionRepository.deleteByAppUserIdAndId(userId, sessionId);
    }

    public List<SessionExerciseCreateResDTO> createSessionExercise(Long userId, Long sessionId, SessionExerciseCreateDTO dto) {
        WorkoutSession session = getWorkoutSessionById(userId, sessionId);
        int nextOrderIndex = sessionExerciseRepository.countSessionExerciseBySessionId(sessionId);

        List<SessionExercise> sessionExercises = new ArrayList<>();
        for (Long exerciseId : dto.exerciseIds()) {
            SessionExercise sessionExercise = new SessionExercise();
            sessionExercise.setSession(session);
            sessionExercise.setExercise(exerciseService.getExerciseById(exerciseId));
            sessionExercise.setOrderIndex(nextOrderIndex++);
            sessionExercises.add(sessionExercise);
        }

        return sessionExerciseRepository.saveAll(sessionExercises).stream()
                .map(SessionExerciseCreateResDTO::from)
                .collect(Collectors.toList());
    }

    public SessionExercise getSessionExerciseById(Long userId, Long id) {
        return sessionExerciseRepository.findByIdAndSessionAppUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session exercise not found"));
    }

    public void updateSessionExerciseNote(Long userId, Long id, SessionExerciseNoteDTO dto) {
        SessionExercise currentSessionExercise = sessionExerciseRepository.findByIdAndSessionAppUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session exercies not found"));

        currentSessionExercise.setNote(dto.note());
        sessionExerciseRepository.save(currentSessionExercise);
    }

    private Map<Long, Integer> countExercisesBySessionIds(List<Long> sessionIds) {
        if (sessionIds.isEmpty()) {
            return Map.of();
        }
        return sessionExerciseRepository.countBySessionIds(sessionIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Long) row[1]).intValue()));
    }

    @Transactional
    public void deleteSessionExercise(Long userId, Long id) {
        sessionExerciseRepository.deleteByIdAndSessionAppUserId(id, userId);
    }
}