package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDetailResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDTO;
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

    public WorkoutSessionService(WorkoutSessionRepository workoutSessionRepository) {
        this.workoutSessionRepository = workoutSessionRepository;
    }

    public List<WorkoutSessionResponse> getAllWorkoutSessions() {
        return workoutSessionRepository.findAll().stream()
                .map(WorkoutSessionResponse::from)
                .collect(Collectors.toList());
    }

    public WorkoutSession getWorkoutSessionById(Long id) {
        return workoutSessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout session not found"));
    }

    public WorkoutSessionDetailResponse getWorkoutSessionDetail(Long id) {
        WorkoutSession session = getWorkoutSessionById(id);
        List<WorkoutSetResponse> sets = session.getWorkoutSets().stream()
                .map(WorkoutSetResponse::from)
                .collect(Collectors.toList());
        return new WorkoutSessionDetailResponse(session.getId(), session.getDate(), session.getNote(), sets);
    }

    public WorkoutSessionResponse saveWorkoutSession(WorkoutSessionDTO dto) {
        WorkoutSession session = new WorkoutSession();
        session.setDate(dto.getDate());
        session.setNote(dto.getNote());
        return WorkoutSessionResponse.from(workoutSessionRepository.save(session));
    }

    public void deleteWorkoutSession(Long id) {
        workoutSessionRepository.deleteById(id);
    }

    public List<WorkoutSetResponse> getWorkoutSetsBySessionId(Long id) {
        return getWorkoutSessionById(id).getWorkoutSets().stream()
                .map(WorkoutSetResponse::from)
                .collect(Collectors.toList());
    }
}
