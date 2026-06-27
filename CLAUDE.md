# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.gymtracker.gymtracker.ClassName"
```

Swagger UI is available at `http://localhost:8080/swagger-ui.html` when the app is running.

## Database

The app uses PostgreSQL via Supabase. DB credentials live in `src/main/resources/application-local.properties` (gitignored profile). The active profile is `local` (set in `application.properties`). Schema is managed by `spring.jpa.hibernate.ddl-auto=update` — no migration files.

## Architecture

Standard Spring Boot layered architecture: **Controller → Service → Repository → Entity**, all under `com.gymtracker.gymtracker`.

### Domain model

- `Exercise` — name + `MuscleGroup` enum; referenced by WorkoutSets and PersonalRecords
- `WorkoutSession` — name, date, note; owns a `List<WorkoutSet>` (cascade all, orphan removal)
- `WorkoutSet` — links an Exercise to a WorkoutSession with weight, reps, note
- `PersonalRecord` — tracks the best weight per exercise; one record per exercise (not a history log)
- `AppUser` — name, username, password, height
- `WeightLog` — body weight entry linked to an AppUser with timestamp (in progress)

### Personal Records auto-update

`PersonalRecordsService.checkAndUpdate(WorkoutSet)` is called automatically inside `WorkoutSetService.createWorkoutSet()` after every save. It updates (or creates) the PR for that exercise if the new set's weight exceeds the stored record. `WorkoutSetResponse.from(set, isPR)` uses the boolean result to mark the response.

### DTOs

- Input DTOs: `XxxDTO` or `XxxRequestDTO`
- Output DTOs: `XxxResponse` or `XxxResponseDTO` with a static `from(entity)` factory method
- Placed in `dto/<domain>/` subpackages

### Error handling

`GlobalExceptionHandler` extends `ResponseEntityExceptionHandler` and handles:
- `IllegalArgumentException` → 409 Conflict
- `@Valid` failures → 400 with field-keyed error map
- Invalid enum values in request body → 400 with list of allowed values
- Not-found cases → thrown as `ResponseStatusException(HttpStatus.NOT_FOUND, ...)` directly in services

### API surface

| Prefix | Resource |
|---|---|
| `/api/workout-sessions` | CRUD for sessions; nested `/{id}/workout-sets` |
| `/api/workout-sets` | CRUD + PATCH for individual sets |
| `/api/exercises` | CRUD for exercises |
| `/api/personal-records` | Read + delete; filterable by `?exerciseId=` |
| `/api/users` | User management |
| `/api/weight-logs` | Body weight logging (in progress) |