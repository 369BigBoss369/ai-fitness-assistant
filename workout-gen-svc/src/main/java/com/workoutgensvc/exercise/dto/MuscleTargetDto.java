package com.workoutgensvc.exercise.dto;

import com.workoutgensvc.exercise.MuscleTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class MuscleTargetDto {
    @NotNull
    @NotBlank
    private String muscle;
    @NotNull
    @NotBlank
    private String intensity;

    public static MuscleTargetDto from(MuscleTarget muscleTarget) {
        return MuscleTargetDto.builder()
                .muscle(muscleTarget.getMuscle() != null ? muscleTarget.getMuscle().toString() : null)
                .intensity(muscleTarget.getIntensity() != null ? muscleTarget.getIntensity().toString() : null)
                .build();
    }
}
