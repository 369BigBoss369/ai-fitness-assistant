package com.workoutgensvc.web;

import com.workoutgensvc.core.AIService;
import com.workoutgensvc.core.AIProcessManager;
import com.workoutgensvc.exercise.dto.ExerciseDto;
import com.workoutgensvc.exercise.Exercise;
import com.workoutgensvc.exercise.ExerciseService;
import com.workoutgensvc.plan.dto.PlanDto;
import com.workoutgensvc.plan.Plan;
import com.workoutgensvc.plan.PlanService;
import com.workoutgensvc.workout.dto.WorkoutDto;
import com.workoutgensvc.workout.Workout;
import com.workoutgensvc.workout.WorkoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class AppWebController {
    private final ExerciseService exerciseService;
    private final WorkoutService workoutService;
    private final PlanService planService;
    private final AIProcessManager aiProcessManager;
    private final AIService aiService;

    public AppWebController(ExerciseService exerciseService, WorkoutService workoutService, PlanService planService, AIProcessManager aiProcessManager, AIService aiService) {
        this.exerciseService = exerciseService;
        this.workoutService = workoutService;
        this.planService = planService;
        this.aiProcessManager = aiProcessManager;
        this.aiService = aiService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "AI Workout Generator");
        model.addAttribute("description", "Generate personalized exercises, workouts, and training plans with AI");
        return "index";
    }

    @GetMapping("/exercises")
    public String exercisesPage(Model model) {
        model.addAttribute("title", "Generate Exercise");
        model.addAttribute("muscleGroups", List.of("Chest", "Back", "Shoulders", "Arms", "Legs", "Core", "Full Body"));
        model.addAttribute("difficulties", List.of("Beginner", "Intermediate", "Advanced"));
        model.addAttribute("equipment", List.of("Bodyweight", "Dumbbells", "Barbell", "Machine", "Cable", "Kettlebell"));
        return "exercises";
    }

    @PostMapping("/exercises/generate")
    public String generateExercise(
            @RequestParam String muscleGroup,
            @RequestParam String difficulty,
            @RequestParam String equipment,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("Generating exercise: muscleGroup={}, difficulty={}, equipment={}",
                    muscleGroup, difficulty, equipment);

            Exercise exercise = exerciseService.generateExercise(muscleGroup, difficulty, equipment);
            ExerciseDto exerciseDto = ExerciseDto.from(exercise);

            model.addAttribute("exercise", exerciseDto);
            model.addAttribute("success", true);
            model.addAttribute("message", "Exercise generated successfully!");

            // Re-populate form data
            model.addAttribute("selectedMuscleGroup", muscleGroup);
            model.addAttribute("selectedDifficulty", difficulty);
            model.addAttribute("selectedEquipment", equipment);
            model.addAttribute("muscleGroups", List.of("Chest", "Back", "Shoulders", "Arms", "Legs", "Core", "Full Body"));
            model.addAttribute("difficulties", List.of("Beginner", "Intermediate", "Advanced"));
            model.addAttribute("equipment", List.of("Bodyweight", "Dumbbells", "Barbell", "Machine", "Cable", "Kettlebell"));

            return "exercises";
        } catch (Exception e) {
            log.error("Error generating exercise", e);
            redirectAttributes.addFlashAttribute("error", "Failed to generate exercise: " + e.getMessage());
            return "redirect:/exercises";
        }
    }

    @GetMapping("/workouts")
    public String workoutsPage(Model model) {
        model.addAttribute("title", "Generate Workout");
        model.addAttribute("workoutTypes", List.of("Strength", "Cardio", "HIIT", "Bodyweight", "Full Body", "Mobility", "Yoga"));
        model.addAttribute("durations", List.of("15", "30", "45", "60", "90"));
        model.addAttribute("fitnessLevels", List.of("Beginner", "Intermediate", "Advanced"));
        model.addAttribute("goals", List.of("Build Muscle", "Lose Weight", "Improve Endurance", "Increase Strength", "Tone Body"));
        return "workouts";
    }

    @PostMapping("/workouts/generate")
    public String generateWorkout(
            @RequestParam String type,
            @RequestParam String duration,
            @RequestParam String fitnessLevel,
            @RequestParam String goals,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("Generating workout: type={}, duration={}, fitnessLevel={}, goals={}",
                    type, duration, fitnessLevel, goals);

            Workout workout = workoutService.generateWorkout(type, duration, fitnessLevel, goals);
            WorkoutDto workoutDto = WorkoutDto.from(workout);

            model.addAttribute("workout", workoutDto);
            model.addAttribute("success", true);
            model.addAttribute("message", "Workout generated successfully!");

            // Re-populate form data
            model.addAttribute("selectedType", type);
            model.addAttribute("selectedDuration", duration);
            model.addAttribute("selectedFitnessLevel", fitnessLevel);
            model.addAttribute("selectedGoals", goals);
            model.addAttribute("workoutTypes", List.of("Strength", "Cardio", "HIIT", "Bodyweight", "Full Body", "Mobility", "Yoga"));
            model.addAttribute("durations", List.of("15", "30", "45", "60", "90"));
            model.addAttribute("fitnessLevels", List.of("Beginner", "Intermediate", "Advanced"));
            model.addAttribute("goals", List.of("Build Muscle", "Lose Weight", "Improve Endurance", "Increase Strength", "Tone Body"));

            return "workouts";
        } catch (Exception e) {
            log.error("Error generating workout", e);
            redirectAttributes.addFlashAttribute("error", "Failed to generate workout: " + e.getMessage());
            return "redirect:/workouts";
        }
    }

    @GetMapping("/plans")
    public String plansPage(Model model) {
        model.addAttribute("title", "Generate Training Plan");
        model.addAttribute("durations", List.of("4", "6", "8", "12"));
        model.addAttribute("frequencies", List.of("3", "4", "5", "6"));
        model.addAttribute("experiences", List.of("Beginner", "Intermediate", "Advanced"));
        model.addAttribute("goals", List.of("Build Muscle", "Lose Weight", "Improve Endurance", "Increase Strength", "Tone Body", "Flexibility"));
        return "plans";
    }

    @PostMapping("/plans/generate")
    public String generatePlan(
            @RequestParam String duration,
            @RequestParam String frequency,
            @RequestParam String goals,
            @RequestParam String experience,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("Generating plan: duration={}, frequency={}, goals={}, experience={}",
                    duration, frequency, goals, experience);

            Plan plan = planService.generatePlan(duration, frequency, goals, experience);
            PlanDto planDto = PlanDto.from(plan);

            model.addAttribute("plan", planDto);
            model.addAttribute("success", true);
            model.addAttribute("message", "Training plan generated successfully!");

            // Re-populate form data
            model.addAttribute("selectedDuration", duration);
            model.addAttribute("selectedFrequency", frequency);
            model.addAttribute("selectedGoals", goals);
            model.addAttribute("selectedExperience", experience);
            model.addAttribute("durations", List.of("4", "6", "8", "12"));
            model.addAttribute("frequencies", List.of("3", "4", "5", "6"));
            model.addAttribute("experiences", List.of("Beginner", "Intermediate", "Advanced"));
            model.addAttribute("goals", List.of("Build Muscle", "Lose Weight", "Improve Endurance", "Increase Strength", "Tone Body", "Flexibility"));

            return "plans";
        } catch (Exception e) {
            log.error("Error generating plan", e);
            redirectAttributes.addFlashAttribute("error", "Failed to generate training plan: " + e.getMessage());
            return "redirect:/plans";
        }
    }


    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("title", "About");
        return "about";
    }
}
