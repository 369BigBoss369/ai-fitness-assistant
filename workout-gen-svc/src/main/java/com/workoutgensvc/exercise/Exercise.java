package com.workoutgensvc.exercise;

import com.workoutgensvc.exercise.enums.ExerciseType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExerciseType type;

    @Builder.Default
    @ElementCollection
    private List<MuscleTarget> muscleGroupTarget = new ArrayList<>();

    @Column(length = 1024)
    private String imageUrl;
    @Column(length = 1024)
    private String videoUrl;
}
