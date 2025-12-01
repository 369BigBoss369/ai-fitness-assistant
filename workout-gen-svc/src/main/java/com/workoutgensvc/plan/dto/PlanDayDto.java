package com.workoutgensvc.plan.dto;

import com.workoutgensvc.plan.PlanDay;
import com.workoutgensvc.workout.Workout;
import com.workoutgensvc.workout.dto.WorkoutDto;
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

public class PlanDayDto {
    @NotNull
    private Integer dayNumber;
    @NotNull
    @NotBlank
    private String type;

    @Builder.Default
    private List<WorkoutDto> workouts = new ArrayList<>();

    public static PlanDayDto from(PlanDay planDay) {
        return PlanDayDto.builder()
                .dayNumber(planDay.getDayNumber())
                .type(planDay.getType().toString())
                .workouts(planDay.getWorkouts() != null ?
                    planDay.getWorkouts().stream()
                        .map(WorkoutDto::from)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}
