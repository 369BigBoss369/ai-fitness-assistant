package com.workoutgensvc.plan.dto;

import com.workoutgensvc.plan.Plan;
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

public class PlanDto {
    @NotNull
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @NotBlank
    private String type;

    @Builder.Default
    private List<PlanDayDto> planDays = new ArrayList<>();

    public static PlanDto from(Plan plan) {
        return PlanDto.builder()
                .name(plan.getName())
                .description(plan.getDescription())
                .type(plan.getType().toString())
                .planDays(plan.getPlanDays() != null ?
                    plan.getPlanDays().stream()
                        .map(PlanDayDto::from)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}
