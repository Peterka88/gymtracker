package com.gymtracker.gymtracker.service;

import com.gymtracker.gymtracker.dto.common.PageResponse;
import com.gymtracker.gymtracker.dto.exercise.ExerciseDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseListResponseDTO;
import com.gymtracker.gymtracker.dto.exercise.ExerciseWorkoutAddResponseDTO;
import com.gymtracker.gymtracker.entity.Equipment;
import com.gymtracker.gymtracker.entity.Exercise;
import com.gymtracker.gymtracker.entity.MuscleGroup;
import com.gymtracker.gymtracker.repository.ExerciseRepository;
import com.gymtracker.gymtracker.repository.WorkoutSetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void createExercise_savesAndReturnsExercise_whenNameIsFree() {
        ExerciseDTO dto = new ExerciseDTO("Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
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
        ExerciseDTO dto = new ExerciseDTO("Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
        when(exerciseRepository.findByName("Bench Press")).thenReturn(Optional.of(exercise(1L, "Bench Press")));

        assertThatThrownBy(() -> exerciseService.createExercise(dto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void updateExercise_savesAndReturnsExercise_whenNameIsFree() {
        ExerciseDTO dto = new ExerciseDTO("Bench Press", MuscleGroup.BACK, Equipment.DUMBBELL);
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
        ExerciseDTO dto = new ExerciseDTO("Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
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
        ExerciseDTO dto = new ExerciseDTO("Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
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
    void deleteExercise_delegatesToRepository() {
        exerciseService.deleteExercise(5L);

        verify(exerciseRepository).deleteById(5L);
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

    private WorkoutSetRepository.LastPerformedProjection mockProjection(Long exerciseId, LocalDateTime lastDate, Double lastWeight) {
        WorkoutSetRepository.LastPerformedProjection projection = org.mockito.Mockito.mock(WorkoutSetRepository.LastPerformedProjection.class);
        when(projection.getExerciseId()).thenReturn(exerciseId);
        when(projection.getLastDate()).thenReturn(lastDate);
        when(projection.getLastWeight()).thenReturn(lastWeight);
        return projection;
    }
}