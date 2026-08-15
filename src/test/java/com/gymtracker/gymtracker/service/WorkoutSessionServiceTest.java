package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.SessionExerciseCreateDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.SessionExerciseNoteDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.requests.WorkoutSessionPatchDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.responses.SessionExerciseCreateResDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.responses.WorkoutSessionFinishResDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.responses.WorkoutSessionPatchResDTO;
import com.gymtracker.gymtracker.dto.newWorkoutSession.responses.WorkoutSessionStartResult;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionDetailResponse;
import com.gymtracker.gymtracker.dto.workoutSession.WorkoutSessionResponse;
import com.gymtracker.gymtracker.entity.AppUser;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.MuscleGroup;
import com.gymtracker.gymtracker.entity.SessionExercise;
import com.gymtracker.gymtracker.entity.WorkoutSession;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import com.gymtracker.gymtracker.repository.SessionExerciseRepository;
import com.gymtracker.gymtracker.repository.WorkoutSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WorkoutSessionServiceTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;
    @Mock
    private SessionExerciseRepository sessionExerciseRepository;
    @Mock
    private AppUserService appUserService;
    @Mock
    private PersonalRecordsService personalRecordsService;
    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private WorkoutSessionService workoutSessionService;

    private WorkoutSession session(Long id, String name) {
        return WorkoutSession.builder()
                .id(id)
                .name(name)
                .startedAt(LocalDateTime.of(2026, 7, 1, 10, 0))
                .build();
    }

    private WorkoutSession session(Long id, String name, String note) {
        return WorkoutSession.builder()
                .id(id)
                .name(name)
                .note(note)
                .startedAt(LocalDateTime.of(2026, 7, 1, 10, 0))
                .build();
    }

    private AppUser appUser(Long id, String username, String name, Double height) {
        return AppUser.builder()
                .id(id)
                .username(username)
                .name(name)
                .height(height)
                .build();
    }

    private Exercise exercise(Long id, String name) {
        return Exercise.builder().id(id).name(name).muscleGroup(MuscleGroup.CHEST).build();
    }

    @Test
    void getWorkoutSessions_appliesExplicitPageAndSize_fromParams(){
        workoutSessionService.getWorkoutSessions(99L,5,3);

        verify(workoutSessionRepository).findAllByAppUserIdOrderByStartedAtDesc(99L, PageRequest.of(3,5));
    }

    @Test
    void getWorkoutSessions_appliesDefaultPageAndSize_whenParamsAreNull(){
        workoutSessionService.getWorkoutSessions(99L,null,null);

        verify(workoutSessionRepository).findAllByAppUserIdOrderByStartedAtDesc(99L, PageRequest.of(0,10));
    }

    @Test
    void getWorkoutSessions_prFlagMappedCorrectly(){
        when(personalRecordsService.getSessionIdsWithPr(99L))
                .thenReturn(Set.of(2L));
        when(workoutSessionRepository.findAllByAppUserIdOrderByStartedAtDesc(99L, PageRequest.of(0,10)))
                .thenReturn(List.of(session(1L, "Without PR"),session(2L, "With PR")));
        when(sessionExerciseRepository.countBySessionIds(List.of(1L,2L)))
                .thenReturn(List.of(new Object[]{1L, 3L}, new Object[]{2L, 5L}));

        List<WorkoutSessionResponse> result = workoutSessionService.getWorkoutSessions(99L,null,null);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).exercises()).isEqualTo(3);
        assertThat(result.get(0).pr()).isFalse();

        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).exercises()).isEqualTo(5);
        assertThat(result.get(1).pr()).isTrue();
    }

    @Test
    void createWorkoutSession_startsNewSession_whenNoActiveSessionExists() {
        when(workoutSessionRepository.findByAppUserIdAndEndedAtIsNull(1L))
                .thenReturn(Optional.empty());
        when(appUserService.getAppUserById(1L))
                .thenReturn(appUser(1L, "testuser", "Test User", 175.0));
        when(workoutSessionRepository.save(any(WorkoutSession.class)))
                .thenAnswer(invocation -> {
                    WorkoutSession session = invocation.getArgument(0);
                    session.setId(99L);
                    return session;
                });

        WorkoutSessionStartResult result = workoutSessionService.createWorkoutSession(1L);

        assertThat(result.created()).isTrue();
        assertThat(result.session().id()).isEqualTo(99L);

        ArgumentCaptor<WorkoutSession> captor = ArgumentCaptor.forClass(WorkoutSession.class);
        verify(workoutSessionRepository).save(captor.capture());
        assertThat(captor.getValue().getAppUser().getId()).isEqualTo(1L);
        assertThat(captor.getValue().getEndedAt()).isNull();
    }

    @Test
    void createWorkoutSession_returnsExistingSession_whenActiveSessionAlreadyExists() {
        WorkoutSession active = session(7L, "Active Session");
        when(workoutSessionRepository.findByAppUserIdAndEndedAtIsNull(1L))
                .thenReturn(Optional.of(active));

        WorkoutSessionStartResult result = workoutSessionService.createWorkoutSession(1L);

        assertThat(result.created()).isFalse();
        assertThat(result.session().id()).isEqualTo(7L);
        verify(workoutSessionRepository, never()).save(any());
        verify(appUserService, never()).getAppUserById(any());
    }

    @Test
    void updateWorkoutSessionNameOrNote_updateName(){
        WorkoutSessionPatchDTO dto = new WorkoutSessionPatchDTO("New Name", null);
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.of(session(1L, "Old Name", "Old Note")));
        when(workoutSessionRepository.save(any(WorkoutSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkoutSessionPatchResDTO result = workoutSessionService.updateWorkoutSessionNameOrNote(1L, 99L, dto);

        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.note()).isEqualTo("Old Note");
        verify(workoutSessionRepository).save(any());
    }

    @Test
    void updateWorkoutSessionNameOrNote_updateNote(){
        WorkoutSessionPatchDTO dto = new WorkoutSessionPatchDTO(null, "New Note");
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.of(session(1L, "Old Name", "Old Note")));
        when(workoutSessionRepository.save(any(WorkoutSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkoutSessionPatchResDTO result = workoutSessionService.updateWorkoutSessionNameOrNote(1L, 99L, dto);

        assertThat(result.name()).isEqualTo("Old Name");
        assertThat(result.note()).isEqualTo("New Note");
        verify(workoutSessionRepository).save(any());
    }

    @Test
    void updateWorkoutSessionNameOrNote_updateNameAndNote(){
        WorkoutSessionPatchDTO dto = new WorkoutSessionPatchDTO("New Name", "New Note");
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.of(session(1L, "Old Name", "Old Note")));
        when(workoutSessionRepository.save(any(WorkoutSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkoutSessionPatchResDTO result = workoutSessionService.updateWorkoutSessionNameOrNote(1L, 99L, dto);

        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.note()).isEqualTo("New Note");
        verify(workoutSessionRepository).save(any());
    }

    @Test
    void updateWorkoutSessionNameOrNote_throwsNotFound_sessionNotFound(){
        WorkoutSessionPatchDTO dto = new WorkoutSessionPatchDTO("New Name", "New Note");
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionService.updateWorkoutSessionNameOrNote(1L, 99L, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("Session not found");
                });
    }

    @Test
    void getWorkoutSessionById_returnsSession_whenFound() {
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.of(session(1L, "Push Day")));

        WorkoutSession result = workoutSessionService.getWorkoutSessionById(99L, 1L);

        assertThat(result.getName()).isEqualTo("Push Day");
    }

    @Test
    void getWorkoutSessionById_throwsNotFound_whenMissing() {
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionService.getWorkoutSessionById(99L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("Workout session not found");
                });
    }

    @Test
    void getWorkoutSessionDetail_setsPrTrue_whenSessionHasPrSet() {
        WorkoutSession fullSession = WorkoutSession.builder()
                .id(1L)
                .name("Push Day")
                .startedAt(LocalDateTime.of(2026, 7, 1, 10, 0))
                .endedAt(LocalDateTime.of(2026, 7, 1, 11, 0))
                .durationMinutes(60)
                .note("Felt strong")
                .build();
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.of(fullSession));

        Exercise benchPress = exercise(5L, "Bench Press");
        SessionExercise sessionExercise = SessionExercise.builder()
                .id(10L)
                .session(fullSession)
                .exercise(benchPress)
                .orderIndex(0)
                .build();
        WorkoutSet topSet = WorkoutSet.builder().id(100L).sessionExercise(sessionExercise).weight(60.0).reps(5).build();
        sessionExercise.setWorkoutSets(List.of(topSet));

        when(sessionExerciseRepository.findAllBySessionIdWithSetsOrderByOrderIndexAsc(1L))
                .thenReturn(List.of(sessionExercise));
        when(personalRecordsService.getPrWorkoutSetIds(99L, 1L))
                .thenReturn(Set.of(100L));

        WorkoutSessionDetailResponse result = workoutSessionService.getWorkoutSessionDetail(99L, 1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.note()).isEqualTo("Felt strong");
        assertThat(result.pr()).isTrue();
        assertThat(result.sessionExercises()).hasSize(1);
        assertThat(result.sessionExercises().getFirst().exerciseId()).isEqualTo(5L);
        assertThat(result.sessionExercises().getFirst().exerciseName()).isEqualTo("Bench Press");
        assertThat(result.sessionExercises().getFirst().workoutSets()).hasSize(1);
        assertThat(result.sessionExercises().getFirst().workoutSets().getFirst().pr()).isTrue();
    }

    @Test
    void getWorkoutSessionDetail_setsPrFalse_whenNoPrWorkoutSets() {
        WorkoutSession fullSession = session(1L, "Push Day");
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.of(fullSession));
        when(sessionExerciseRepository.findAllBySessionIdWithSetsOrderByOrderIndexAsc(1L))
                .thenReturn(List.of());
        when(personalRecordsService.getPrWorkoutSetIds(99L, 1L))
                .thenReturn(Set.of());

        WorkoutSessionDetailResponse result = workoutSessionService.getWorkoutSessionDetail(99L, 1L);

        assertThat(result.pr()).isFalse();
        assertThat(result.sessionExercises()).isEmpty();
    }

    @Test
    void finishWorkoutSession_setsEndedAtAndDuration_whenNotAlreadyFinished() {
        LocalDateTime startedAt = LocalDateTime.now().minusMinutes(45);
        WorkoutSession activeSession = WorkoutSession.builder()
                .id(1L)
                .name("Push Day")
                .startedAt(startedAt)
                .build();
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.of(activeSession));
        when(workoutSessionRepository.save(any(WorkoutSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime before = LocalDateTime.now();
        WorkoutSessionFinishResDTO result = workoutSessionService.finishWorkoutSession(99L, 1L);
        LocalDateTime after = LocalDateTime.now();

        assertThat(result.endedAt()).isAfterOrEqualTo(before).isBeforeOrEqualTo(after);
        assertThat(result.durationMinutes())
                .isEqualTo((int) Duration.between(startedAt, result.endedAt()).toMinutes());
    }

    @Test
    void finishWorkoutSession_throwsIllegalArgument_whenAlreadyFinished() {
        WorkoutSession finishedSession = WorkoutSession.builder()
                .id(1L)
                .name("Push Day")
                .startedAt(LocalDateTime.of(2026, 7, 1, 10, 0))
                .endedAt(LocalDateTime.of(2026, 7, 1, 11, 0))
                .build();
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.of(finishedSession));

        assertThatThrownBy(() -> workoutSessionService.finishWorkoutSession(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Workout session already finished");

        verify(workoutSessionRepository, never()).save(any());
    }

    @Test
    void deleteWorkoutSession_delegatesToRepository() {
        workoutSessionService.deleteWorkoutSession(99L, 1L);

        verify(workoutSessionRepository).deleteByAppUserIdAndId(99L, 1L);
    }

    @Test
    void createSessionExercise_assignsIncrementingOrderIndex_continuingFromExistingCount() {
        WorkoutSession existingSession = session(1L, "Push Day");
        when(workoutSessionRepository.findByAppUserIdAndId(99L, 1L))
                .thenReturn(Optional.of(existingSession));
        when(sessionExerciseRepository.countSessionExerciseBySessionId(1L))
                .thenReturn(2);

        Exercise benchPress = exercise(10L, "Bench Press");
        Exercise squat = exercise(20L, "Squat");
        when(exerciseService.getExerciseById(10L)).thenReturn(benchPress);
        when(exerciseService.getExerciseById(20L)).thenReturn(squat);

        when(sessionExerciseRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<SessionExercise> toSave = invocation.getArgument(0);
            for (int i = 0; i < toSave.size(); i++) {
                toSave.get(i).setId(100L + i);
            }
            return toSave;
        });

        SessionExerciseCreateDTO dto = new SessionExerciseCreateDTO(List.of(10L, 20L));

        List<SessionExerciseCreateResDTO> result = workoutSessionService.createSessionExercise(99L, 1L, dto);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).exerciseId()).isEqualTo(10L);
        assertThat(result.get(0).orderIndex()).isEqualTo(2);
        assertThat(result.get(1).exerciseId()).isEqualTo(20L);
        assertThat(result.get(1).orderIndex()).isEqualTo(3);
    }

    @Test
    void getSessionExerciseById_returnsSessionExercise_whenFound() {
        SessionExercise sessionExercise = SessionExercise.builder().id(5L).orderIndex(0).build();
        when(sessionExerciseRepository.findByIdAndSessionAppUserId(5L, 99L))
                .thenReturn(Optional.of(sessionExercise));

        SessionExercise result = workoutSessionService.getSessionExerciseById(99L, 5L);

        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void getSessionExerciseById_throwsNotFound_whenMissing() {
        when(sessionExerciseRepository.findByIdAndSessionAppUserId(5L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionService.getSessionExerciseById(99L, 5L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void updateSessionExerciseNote_updatesNoteAndSaves() {
        SessionExercise sessionExercise = SessionExercise.builder().id(5L).note("Old note").build();
        when(sessionExerciseRepository.findByIdAndSessionAppUserId(5L, 99L))
                .thenReturn(Optional.of(sessionExercise));

        workoutSessionService.updateSessionExerciseNote(99L, 5L, new SessionExerciseNoteDTO("New note"));

        ArgumentCaptor<SessionExercise> captor = ArgumentCaptor.forClass(SessionExercise.class);
        verify(sessionExerciseRepository).save(captor.capture());
        assertThat(captor.getValue().getNote()).isEqualTo("New note");
    }

    @Test
    void updateSessionExerciseNote_throwsNotFound_whenMissing() {
        when(sessionExerciseRepository.findByIdAndSessionAppUserId(5L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionService.updateSessionExerciseNote(99L, 5L, new SessionExerciseNoteDTO("New note")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(sessionExerciseRepository, never()).save(any());
    }

    @Test
    void deleteSessionExercise_delegatesToRepository() {
        workoutSessionService.deleteSessionExercise(99L, 5L);

        verify(sessionExerciseRepository).deleteByIdAndSessionAppUserId(5L, 99L);
    }
}
