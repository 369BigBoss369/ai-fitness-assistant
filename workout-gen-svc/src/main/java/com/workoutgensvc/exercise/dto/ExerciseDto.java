package com.workoutgensvc.exercise.dto;

import com.workoutgensvc.exercise.Exercise;
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

public class ExerciseDto {
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String type;

    @Builder.Default
    private List<MuscleTargetDto> muscleTargets =  new ArrayList<>();

    private String imageUrl;
    private String videoUrl;

    public static ExerciseDto from(Exercise exercise) {
        return ExerciseDto.builder()
                .name(exercise.getName())
                .type(exercise.getType().toString())
                .muscleTargets(exercise.getMuscleGroupTarget() != null ?
                    exercise.getMuscleGroupTarget().stream()
                        .map(MuscleTargetDto::from)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .imageUrl(exercise.getImageUrl())
                .videoUrl(exercise.getVideoUrl())
                .build();
    }
}
