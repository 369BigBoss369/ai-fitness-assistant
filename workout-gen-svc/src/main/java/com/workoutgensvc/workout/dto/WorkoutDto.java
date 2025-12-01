package com.workoutgensvc.workout.dto;

import com.workoutgensvc.workout.Workout;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class WorkoutDto {
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String type;

    @Builder.Default
    private List<WorkoutExerciseDto> exercises = new ArrayList<>();

    public static WorkoutDto from(Workout workout) {
        return WorkoutDto.builder()
                .name(workout.getName())
                .type(workout.getType().toString())
                .exercises(workout.getWorkoutExercises() != null ?
                    workout.getWorkoutExercises().stream()
                        .map(WorkoutExerciseDto::from)
                        .sorted((a, b) -> Integer.compare(a.getNumber(), b.getNumber()))
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}
