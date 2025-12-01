package com.workoutgensvc.workout;

import com.workoutgensvc.workout.enums.WorkoutType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

@Entity
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkoutType type;

    @Builder.Default
    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL)
    private Set<WorkoutExercise> workoutExercises = new HashSet<>();
}
