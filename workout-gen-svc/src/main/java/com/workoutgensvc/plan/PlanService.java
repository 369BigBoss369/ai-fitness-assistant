package com.workoutgensvc.plan;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.workoutgensvc.core.AIService;
import com.workoutgensvc.plan.enums.PlanDayType;
import com.workoutgensvc.plan.enums.PlanType;
import com.workoutgensvc.workout.Workout;
import com.workoutgensvc.workout.WorkoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PlanService {
    private final PlanRepository planRepository;
    private final AIService aiService;
    private final WorkoutService workoutService;
    private final Gson gson;

    @Autowired
    public PlanService(PlanRepository planRepository, AIService aiService, WorkoutService workoutService, Gson gson) {
        this.planRepository = planRepository;
        this.aiService = aiService;
        this.workoutService = workoutService;
        this.gson = gson;
    }

    @Transactional
    public Plan generatePlan(String duration, String frequency, String goals, String experience) {
        try {
            String response = aiService.generatePlan(duration, frequency, goals, experience, workoutService.getAllNames());
            response = response.replaceAll("```", "").replace("json\n", "");
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

            Plan plan = Plan.builder()
                    .name(jsonObject.get("name").getAsString())
                    .type(parsePlanType(goals))
                    .build();

            List<PlanDay> planDays = new ArrayList<>();

            if (jsonObject.has("planDays")) {
                JsonArray planDaysArray = jsonObject.getAsJsonArray("planDays");

                for (JsonElement planDayElement : planDaysArray) {
                    try {
                        JsonObject jsonPlanDay = planDayElement.getAsJsonObject();

                        PlanDay planDay = PlanDay.builder()
                                .dayNumber(jsonPlanDay.get("dayNumber").getAsInt())
                                .type(PlanDayType.valueOf(jsonPlanDay.get("type").getAsString()))
                                .plan(plan)
                                .build();

                        if (jsonPlanDay.has("workoutNames")) {
                            List<Workout> associatedWorkouts = new ArrayList<>();
                            JsonArray workoutNamesArray = jsonPlanDay.getAsJsonArray("workoutNames");

                            for (JsonElement workoutNameElement : workoutNamesArray) {
                                String workoutName = workoutNameElement.getAsString();
                                Optional<Workout> workoutOptional = workoutService.getByName(workoutName);

                                if (workoutOptional.isPresent()) {
                                    associatedWorkouts.add(workoutOptional.get());
                                } else {
                                    log.warn("Workout '{}' not found for plan day {}. Available workouts: {}",
                                            workoutName, planDay.getDayNumber(),
                                            workoutService.getAllNames());
                                }
                            }
                            planDay.setWorkouts(associatedWorkouts);
                        }

                        planDays.add(planDay);
                    } catch (Exception e) {
                        log.warn("Failed to process plan day: {}", planDayElement, e);
                    }
                }
            }

            plan.setPlanDays(planDays);
            Plan savedPlan = planRepository.save(plan);

            savedPlan.getPlanDays().forEach(planDay -> {
                planDay.getWorkouts().forEach(workout -> {
                    workout.getWorkoutExercises().forEach(workoutExercise -> {
                        if (workoutExercise.getExercise() != null) {
                            workoutExercise.getExercise().getName(); // Touch basic properties
                            workoutExercise.getExercise().getMuscleGroupTarget().size();
                        }
                    });
                });
            });

            return savedPlan;

        } catch (Exception e) {
            log.error("Failed to parse AI response for plan generation", e);
            throw new RuntimeException("Failed to generate plan from AI response", e);
        }
    }

    private PlanType parsePlanType(String goals) {
        if (goals == null || goals.trim().isEmpty()) {
            throw new IllegalArgumentException("Plan goals cannot be null or empty");
        }

        // Normalize the input: convert to uppercase and replace spaces with underscores
        String normalizedGoals = goals.trim().toUpperCase().replace(" ", "_");

        try {
            return PlanType.valueOf(normalizedGoals);
        } catch (IllegalArgumentException e) {
            // If direct conversion fails, try to find by display name
            for (PlanType planType : PlanType.values()) {
                if (planType.getDisplayName().equalsIgnoreCase(goals.trim())) {
                    return planType;
                }
            }
            // Special mappings for common variations
            switch (goals.toLowerCase().trim()) {
                case "lose weight":
                case "weight loss":
                    return PlanType.WEIGHT_LOSS;
                case "build muscle":
                case "muscle building":
                case "increase strength":
                    return PlanType.STRENGTH;
                case "improve endurance":
                case "endurance":
                    return PlanType.ENDURANCE;
                case "flexibility":
                case "tone body":
                    return PlanType.FLEXIBILITY;
                default:
                    throw new IllegalArgumentException("Unknown plan goals: " + goals +
                        ". Valid goals are: " + java.util.Arrays.toString(PlanType.values()));
            }
        }
    }
}