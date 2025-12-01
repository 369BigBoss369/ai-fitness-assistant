package com.workoutgensvc.web.rest;

import com.workoutgensvc.exercise.dto.ExerciseDto;
import com.workoutgensvc.exercise.Exercise;
import com.workoutgensvc.exercise.ExerciseService;
import com.workoutgensvc.plan.dto.PlanDto;
import com.workoutgensvc.plan.Plan;
import com.workoutgensvc.plan.PlanService;
import com.workoutgensvc.workout.dto.WorkoutDto;
import com.workoutgensvc.workout.Workout;
import com.workoutgensvc.workout.WorkoutService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@Validated
public class AppRestController {
    private final ExerciseService exerciseService;
    private final WorkoutService workoutService;
    private final PlanService planService;

    @Autowired
    public AppRestController(ExerciseService exerciseService, WorkoutService workoutService, PlanService planService) {
        this.exerciseService = exerciseService;
        this.workoutService = workoutService;
        this.planService = planService;
    }

    @PostMapping("/exercises")
    public ResponseEntity<ExerciseDto> generateExercise(@RequestParam @NotBlank(message = "Muscle group is required") String muscleGroup, @RequestParam(defaultValue = "intermediate") String difficulty, @RequestParam(defaultValue = "bodyweight") String equipment) {
        try {
            log.info("Generating AI exercise for muscleGroup: {}, difficulty: {}, equipment: {}", muscleGroup, difficulty, equipment);

            Exercise exercise = exerciseService.generateExercise(muscleGroup, difficulty, equipment);
            ExerciseDto exerciseDto = ExerciseDto.from(exercise);

            return ResponseEntity.ok(exerciseDto);
        } catch (Exception e) {
            log.error("Failed to generate exercise", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/workouts")
    public ResponseEntity<WorkoutDto> generateWorkout(@RequestParam @NotBlank(message = "Workout type is required") String type, @RequestParam(defaultValue = "45") String duration, @RequestParam(defaultValue = "intermediate") String fitnessLevel, @RequestParam @NotBlank(message = "Goals are required") String goals) {
        try {
            log.info("Generating AI workout - type: {}, duration: {}, fitnessLevel: {}, goals: {}", type, duration, fitnessLevel, goals);

            Workout workout = workoutService.generateWorkout(type, duration, fitnessLevel, goals);
            WorkoutDto workoutDto = WorkoutDto.from(workout);

            return ResponseEntity.ok(workoutDto);
        } catch (Exception e) {
            log.error("Failed to generate workout", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/plans")
    public ResponseEntity<PlanDto> generatePlan(@RequestParam(defaultValue = "4") String duration, @RequestParam(defaultValue = "3") String frequency, @RequestParam @NotBlank(message = "Goals are required") String goals, @RequestParam(defaultValue = "intermediate") String experience) {
        try {
            log.info("Generating AI plan - duration: {} weeks, frequency: {} days/week, goals: {}, experience: {}", duration, frequency, goals, experience);

            Plan plan = planService.generatePlan(duration, frequency, goals, experience);
            PlanDto planDto = PlanDto.from(plan);

            return ResponseEntity.ok(planDto);
        } catch (Exception e) {
            log.error("Failed to generate plan", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}