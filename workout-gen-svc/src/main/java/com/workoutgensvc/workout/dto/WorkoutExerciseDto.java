package com.workoutgensvc.workout.dto;

import com.workoutgensvc.workout.WorkoutExercise;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class WorkoutExerciseDto {
    @NotNull
    private Integer number;

    private Integer reps;
    private Double weight;
    private Integer duration;

    @NotNull
    private Double burnedCalories;

    @NotNull
    @NotBlank
    private String exerciseName;

    public static WorkoutExerciseDto from(WorkoutExercise workoutExercise) {
        return WorkoutExerciseDto.builder()
                .number(workoutExercise.getNumber())
                .reps(workoutExercise.getReps())
                .weight(workoutExercise.getWeight())
                .duration(workoutExercise.getDuration())
                .burnedCalories(workoutExercise.getBurnedCalories())
                .exerciseName(workoutExercise.getExercise() != null ? workoutExercise.getExercise().getName() : null)
                .build();
    }
}
