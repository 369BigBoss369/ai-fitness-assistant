package com.workoutgensvc.exercise;

import com.workoutgensvc.exercise.enums.Intensity;
import com.workoutgensvc.exercise.enums.MuscleType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MuscleTarget {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MuscleType muscle;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Intensity intensity;
}