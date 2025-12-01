package com.workoutgensvc.plan;

import com.workoutgensvc.plan.enums.PlanDayType;
import com.workoutgensvc.workout.Workout;
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
@Table(name = "plan_days")
public class PlanDay {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer dayNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanDayType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id",  nullable = false)
    private Plan plan;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = @JoinColumn(name = "plan_day_id"),
            inverseJoinColumns = @JoinColumn(name = "workout_id")
    )
    private List<Workout> workouts = new ArrayList<>();
}