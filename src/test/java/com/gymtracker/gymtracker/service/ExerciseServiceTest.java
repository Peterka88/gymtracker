package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.common.PageResponse;
import com.gymtracker.gymtracker.dto.exercise.ExerciseCreateReqDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseHistoryDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseListResponseDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseStatsDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseWorkoutAddResponseDTO;
import com.gymtracker.gymtracker.dto.exercise.ProgressData;
import com.gymtracker.gymtracker.entity.Equipment;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.MuscleGroup;
import com.gymtracker.gymtracker.entity.SessionExercise;
import com.gymtracker.gymtracker.entity.WorkoutSession;
import com.gymtracker.gymtracker.entity.WorkoutSet;
import com.gymtracker.gymtracker.repository.ExerciseRepository;
import com.gymtracker.gymtracker.repository.SessionExerciseRepository;
import com.gymtracker.gymtracker.repository.WorkoutSessionRepository;
import com.gymtracker.gymtracker.repository.WorkoutSetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private SessionExerciseRepository sessionExerciseRepository;

    @Mock
    private PersonalRecordsService personalRecordsService;

    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise exercise(Long id, String name) {
        return Exercise.builder()
                .id(id)
                .name(name)
                .muscleGroup(MuscleGroup.CHEST)
                .equipment(Equipment.BARBELL)
                .build();
    }

    private WorkoutSession session(Long id, String name, LocalDateTime startedAt) {
        return WorkoutSession.builder().id(id).name(name).startedAt(startedAt).build();
    }

    private SessionExercise sessionExercise(WorkoutSession session) {
        return SessionExercise.builder().session(session).build();
    }

    private WorkoutSet set(SessionExercise sessionExercise, double weight, int reps) {
        return WorkoutSet.builder().sessionExercise(sessionExercise).weight(weight).reps(reps).build();
    }

    @Test
    void createExercise_savesAndReturnsExercise_whenNameIsFree() {
        ExerciseCreateReqDTO dto = new ExerciseCreateReqDTO("Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
        when(exerciseRepository.findByName("Bench Press")).thenReturn(Optional.empty());
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> {
            Exercise toSave = invocation.getArgument(0);
            toSave.setId(1L);
            return toSave;
        });

        Exercise result = exerciseService.createExercise(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Bench Press");
        assertThat(result.getMuscleGroup()).isEqualTo(MuscleGroup.CHEST);
        assertThat(result.getEquipment()).isEqualTo(Equipment.BARBELL);
    }

    @Test
    void createExercise_throwsBadRequest_whenNameIsOccupied() {
        ExerciseCreateReqDTO dto = new ExerciseCreateReqDTO("Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
        when(exerciseRepository.findByName("Bench Press")).thenReturn(Optional.of(exercise(1L, "Bench Press")));

        assertThatThrownBy(() -> exerciseService.createExercise(dto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void updateExercise_savesAndReturnsExercise_whenNameIsFree() {
        ExerciseCreateReqDTO dto = new ExerciseCreateReqDTO("Bench Press", MuscleGroup.BACK, Equipment.DUMBBELL);
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise(1L, "Old Name")));
        when(exerciseRepository.findByName("Bench Press")).thenReturn(Optional.empty());
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Exercise result = exerciseService.updateExercise(1L, dto);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Bench Press");
        assertThat(result.getMuscleGroup()).isEqualTo(MuscleGroup.BACK);
        assertThat(result.getEquipment()).isEqualTo(Equipment.DUMBBELL);
    }

    @Test
    void updateExercise_throwsBadRequest_whenNameIsOccupied() {
        ExerciseCreateReqDTO dto = new ExerciseCreateReqDTO("Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise(1L, "Squat")));
        when(exerciseRepository.findByName("Bench Press")).thenReturn(Optional.of(exercise(2L, "Bench Press")));

        assertThatThrownBy(() -> exerciseService.updateExercise(1L, dto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void updateExercise_throwsNotFound_whenExerciseMissing() {
        ExerciseCreateReqDTO dto = new ExerciseCreateReqDTO("Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
        when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseService.updateExercise(99L, dto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void getExerciseById_returnsExercise_whenFound() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise(1L, "Squat")));

        Exercise result = exerciseService.getExerciseById(1L);

        assertThat(result.getName()).isEqualTo("Squat");
    }

    @Test
    void getExerciseById_throwsNotFound_whenMissing() {
        when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseService.getExerciseById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteExercise_deletesSessionExercisesThenExercise_whenExists() {
        when(exerciseRepository.existsById(5L)).thenReturn(true);

        exerciseService.deleteExercise(5L);

        verify(sessionExerciseRepository).deleteByExerciseId(5L);
        verify(exerciseRepository).deleteById(5L);
    }

    @Test
    void deleteExercise_throwsNotFound_whenMissing() {
        when(exerciseRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> exerciseService.deleteExercise(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(sessionExerciseRepository, never()).deleteByExerciseId(any());
        verify(exerciseRepository, never()).deleteById(any());
    }

    @Test
    void countExercises_returnsRepositoryCount() {
        when(exerciseRepository.count()).thenReturn(7L);

        assertThat(exerciseService.countExercises()).isEqualTo(7);
    }

    @Test
    void getAll_marksLastPerformedData_whenProjectionExists() {
        Exercise ex = exercise(1L, "Deadlift");
        Page<Exercise> page = new PageImpl<>(List.of(ex));
        when(exerciseRepository.search(isNull(), isNull(), any())).thenReturn(page);

        LocalDateTime lastDate = LocalDateTime.of(2026, 7, 1, 10, 0);
        WorkoutSetRepository.LastPerformedProjection projection = mockProjection(1L, lastDate, 120.0);
        when(workoutSetRepository.findLastPerformedByExercise(List.of(1L))).thenReturn(List.of(projection));

        PageResponse<ExerciseListResponseDTO> result = exerciseService.getAll(10, 0, null, null);

        assertThat(result.content()).hasSize(1);
        ExerciseListResponseDTO dto = result.content().getFirst();
        assertThat(dto.lastDate()).isEqualTo(lastDate.toLocalDate());
        assertThat(dto.lastWeight()).isEqualTo(120.0);
    }

    @Test
    void getAll_leavesLastPerformedNull_whenNoProjectionForExercise() {
        Exercise ex = exercise(1L, "Deadlift");
        Page<Exercise> page = new PageImpl<>(List.of(ex));
        when(exerciseRepository.search(isNull(), isNull(), any())).thenReturn(page);
        when(workoutSetRepository.findLastPerformedByExercise(List.of(1L))).thenReturn(List.of());

        PageResponse<ExerciseListResponseDTO> result = exerciseService.getAll(10, 0, null, null);

        ExerciseListResponseDTO dto = result.content().getFirst();
        assertThat(dto.lastDate()).isNull();
        assertThat(dto.lastWeight()).isNull();
    }

    @Test
    void getAll_skipsLastPerformedLookup_whenPageIsEmpty() {
        when(exerciseRepository.search(isNull(), isNull(), any())).thenReturn(Page.empty());

        PageResponse<ExerciseListResponseDTO> result = exerciseService.getAll(10, 0, null, null);

        assertThat(result.content()).isEmpty();
        verify(workoutSetRepository, never()).findLastPerformedByExercise(anyList());
    }

    @Test
    void getAll_buildsLowercaseSearchPattern_andPassesMuscleGroups() {
        when(exerciseRepository.search(eq("%bench%"), eq(List.of(MuscleGroup.CHEST)), any()))
                .thenReturn(Page.empty());

        exerciseService.getAll(10, 0, "Bench", List.of(MuscleGroup.CHEST));

        verify(exerciseRepository, times(1)).search(eq("%bench%"), eq(List.of(MuscleGroup.CHEST)), any());
    }

    @Test
    void getAll_passesNullSearch_whenSearchIsBlank() {
        when(exerciseRepository.search(isNull(), isNull(), any())).thenReturn(Page.empty());

        exerciseService.getAll(10, 0, "   ", List.of());

        verify(exerciseRepository).search(isNull(), isNull(), any());
    }

    @Test
    void getAllForWorkout_mapsPageToWorkoutAddResponse() {
        Exercise ex = exercise(2L, "Pull Up");
        when(exerciseRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(ex)));

        PageResponse<ExerciseWorkoutAddResponseDTO> result = exerciseService.getAllForWorkout(10, 0);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().name()).isEqualTo("Pull Up");
    }

    @Test
    void getExerciseStats_computesPrAndProgressData_groupedBySessionSortedByDate() {
        Exercise ex = exercise(1L, "Bench Press");
        WorkoutSession earlierSession = session(10L, "Push Day", LocalDateTime.of(2026, 7, 1, 10, 0));
        WorkoutSession laterSession = session(20L, "Push Day", LocalDateTime.of(2026, 7, 10, 10, 0));
        SessionExercise seEarlier = sessionExercise(earlierSession);
        SessionExercise seLater = sessionExercise(laterSession);

        List<WorkoutSet> sets = List.of(
                set(seEarlier, 50.0, 10),
                set(seEarlier, 60.0, 5),
                set(seLater, 70.0, 3)
        );

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(ex));
        when(workoutSetRepository.findAllForExerciseAndAppUser(1L, 99L)).thenReturn(sets);

        ExerciseStatsDTO result = exerciseService.getExerciseStats(1L, 99L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.pr()).isEqualTo(70.0);
        assertThat(result.totalWorkouts()).isEqualTo(2);
        assertThat(result.lastTraining()).isEqualTo(70.0);
        assertThat(result.progressData()).hasSize(2);

        ProgressData first = result.progressData().get(0);
        assertThat(first.date()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(first.weight()).isEqualTo(60.0);
        assertThat(first.volume()).isEqualTo(800.0);
        assertThat(first.estimated1RM()).isCloseTo(70.0, within(0.001));

        ProgressData second = result.progressData().get(1);
        assertThat(second.date()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(second.weight()).isEqualTo(70.0);
        assertThat(second.volume()).isEqualTo(210.0);
        assertThat(second.estimated1RM()).isCloseTo(77.0, within(0.001));
    }

    @Test
    void getExerciseStats_throwsNotFound_whenExerciseMissing() {
        when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseService.getExerciseStats(99L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(workoutSetRepository, never()).findAllForExerciseAndAppUser(any(), any());
    }

    @Test
    void getExerciseHistory_mapsAndSortsSessionsByDateDescending_withPrFlag() {
        WorkoutSession pushDay = session(10L, "Push Day", LocalDateTime.of(2026, 7, 1, 10, 0));
        WorkoutSession pullDay = session(20L, "Pull Day", LocalDateTime.of(2026, 7, 10, 10, 0));
        SessionExercise sePush = sessionExercise(pushDay);
        SessionExercise sePull = sessionExercise(pullDay);

        List<WorkoutSet> sets = List.of(
                set(sePush, 50.0, 10),
                set(sePush, 60.0, 5),
                set(sePull, 70.0, 3)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<WorkoutSession> page = new PageImpl<>(List.of(pushDay, pullDay), pageable, 2);

        when(workoutSessionRepository.findSessionsForExercise(1L, 99L, pageable)).thenReturn(page);
        when(workoutSetRepository.findAllBySessionIds(1L, List.of(10L, 20L))).thenReturn(sets);
        when(personalRecordsService.getSessionIdsWithPr(99L)).thenReturn(Set.of(20L));

        PageResponse<ExerciseHistoryDTO> result = exerciseService.getExerciseHistory(1L, 99L, 10, 0);

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);

        ExerciseHistoryDTO mostRecent = result.content().get(0);
        assertThat(mostRecent.id()).isEqualTo(20L);
        assertThat(mostRecent.name()).isEqualTo("Pull Day");
        assertThat(mostRecent.pr()).isTrue();
        assertThat(mostRecent.date()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(mostRecent.bestWeight()).isEqualTo(70.0);
        assertThat(mostRecent.setCount()).isEqualTo(1);
        assertThat(mostRecent.totalReps()).isEqualTo(3);
        assertThat(mostRecent.volume()).isEqualTo(210.0);

        ExerciseHistoryDTO older = result.content().get(1);
        assertThat(older.id()).isEqualTo(10L);
        assertThat(older.pr()).isFalse();
        assertThat(older.date()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(older.bestWeight()).isEqualTo(60.0);
        assertThat(older.setCount()).isEqualTo(2);
        assertThat(older.totalReps()).isEqualTo(15);
        assertThat(older.volume()).isEqualTo(800.0);
    }

    @Test
    void getExerciseHistory_returnsEmptyPage_whenNoSessionsForExercise() {
        Pageable pageable = PageRequest.of(0, 10);
        when(workoutSessionRepository.findSessionsForExercise(1L, 99L, pageable)).thenReturn(Page.empty(pageable));
        when(workoutSetRepository.findAllBySessionIds(1L, List.of())).thenReturn(List.of());
        when(personalRecordsService.getSessionIdsWithPr(99L)).thenReturn(Set.of());

        PageResponse<ExerciseHistoryDTO> result = exerciseService.getExerciseHistory(1L, 99L, 10, 0);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    private WorkoutSetRepository.LastPerformedProjection mockProjection(Long exerciseId, LocalDateTime lastDate, Double lastWeight) {
        WorkoutSetRepository.LastPerformedProjection projection = org.mockito.Mockito.mock(WorkoutSetRepository.LastPerformedProjection.class);
        when(projection.getExerciseId()).thenReturn(exerciseId);
        when(projection.getLastDate()).thenReturn(lastDate);
        when(projection.getLastWeight()).thenReturn(lastWeight);
        return projection;
    }
}