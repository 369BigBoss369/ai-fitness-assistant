package com.workoutgensvc.workout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.workoutgensvc.core.AIService;
import com.workoutgensvc.exercise.Exercise;
import com.workoutgensvc.exercise.ExerciseService;
import com.workoutgensvc.exercise.enums.ExerciseType;
import com.workoutgensvc.workout.enums.WorkoutType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final AIService aiService;
    private final ExerciseService exerciseService;
    private final Gson gson;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository, AIService aiService, ExerciseService exerciseService, Gson gson) {
        this.workoutRepository = workoutRepository;
        this.aiService = aiService;
        this.exerciseService = exerciseService;
        this.gson = gson;
    }

    @Transactional
    public Workout generateWorkout(String type, String duration, String fitnessLevel, String goals) {
        try {
            String response = aiService.generateWorkout(type, duration, fitnessLevel, goals, exerciseService.getAllNames());
            response = response.replaceAll("```", "").replace("json\n", "");
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

            Workout workout = Workout.builder()
                    .name(jsonObject.get("name").getAsString())
                    .type(parseWorkoutType(type))
                    .build();

            Set<WorkoutExercise> workoutExercises = new HashSet<>();
            if (jsonObject.has("workoutExercises")) {
                JsonArray workoutExercisesArray = jsonObject.getAsJsonArray("workoutExercises");

                for (JsonElement workoutExerciseElement : workoutExercisesArray) {
                    try {
                        JsonObject jsonWorkoutExercise = workoutExerciseElement.getAsJsonObject();
                        String exerciseName = jsonWorkoutExercise.get("exerciseName").getAsString();

                        Optional<Exercise> optional = exerciseService.getByName(exerciseName);
                        Exercise exercise;
                        if (optional.isPresent()) {
                            exercise = optional.get();
                        } else {
                            exercise = Exercise.builder()
                                    .name(exerciseName)
                                    .type(ExerciseType.STRENGTH) // Default type
                                    .videoUrl("https://example.com/placeholder-video") // Placeholder URL
                                    .build();
                            log.info("Created new exercise '{}' referenced by AI-generated workout", exerciseName);
                        }

                        WorkoutExercise workoutExercise = WorkoutExercise.builder()
                                .number(jsonWorkoutExercise.get("number").getAsInt())
                                .reps(jsonWorkoutExercise.has("reps") ? jsonWorkoutExercise.get("reps").getAsInt() : null)
                                .weight(jsonWorkoutExercise.has("weight") ? jsonWorkoutExercise.get("weight").getAsDouble() : null)
                                .duration(jsonWorkoutExercise.has("duration") ? jsonWorkoutExercise.get("duration").getAsInt() : null)
                                .burnedCalories(jsonWorkoutExercise.has("burnedCalories") ?
                                    jsonWorkoutExercise.get("burnedCalories").getAsDouble() :
                                    calculateCalories(jsonWorkoutExercise, Integer.parseInt(duration)))
                                .workout(workout)
                                .exercise(exercise)
                                .build();

                        workoutExercises.add(workoutExercise);
                    } catch (Exception e) {
                        log.warn("Failed to process workout exercise: {}", workoutExerciseElement, e);
                    }
                }
            }

            workout.setWorkoutExercises(workoutExercises);
            return workoutRepository.save(workout);

        } catch (Exception e) {
            log.error("Failed to parse AI response for workout generation", e);
            throw new RuntimeException("Failed to generate workout from AI response", e);
        }
    }

    private Double calculateCalories(JsonObject exerciseObj, int workoutDuration) {
        // Basic calorie calculation - can be enhanced with more sophisticated logic
        double baseCalories = 50.0; // Base calories per exercise

        if (exerciseObj.has("sets") && exerciseObj.has("reps")) {
            int sets = exerciseObj.get("sets").getAsInt();
            int reps = exerciseObj.has("reps") ? exerciseObj.get("reps").getAsInt() : 10;
            baseCalories = sets * reps * 2.0; // Rough estimate
        }

        return Math.max(baseCalories, 25.0); // Minimum 25 calories per exercise
    }

    @Transactional(readOnly = true)
    public String getAllNames() {
        List<String> workouts = workoutRepository.findAll()
                .stream().map(Workout::getName)
                .toList();

        return workouts.isEmpty()
                ? "No existing exercises available - you can suggest common exercises"
                : "Available exercises: " + String.join(", ", workouts);
    }

    @Transactional(readOnly = true)
    public Optional<Workout> getByName(String name) {
        List<Workout> workouts = workoutRepository.findByNameIgnoreCase(name);

        if (workouts.isEmpty()) {
            return Optional.empty();
        } else if (workouts.size() == 1) {
            return Optional.of(workouts.get(0));
        } else {
            // Multiple workouts with same name - return the first one
            log.warn("Multiple workouts found with name '{}', returning first match: {}", name, workouts.get(0).getName());
            return Optional.of(workouts.get(0));
        }
    }

    private WorkoutType parseWorkoutType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Workout type cannot be null or empty");
        }

        // Normalize the input: convert to uppercase and replace spaces with underscores
        String normalizedType = type.trim().toUpperCase().replace(" ", "_");

        try {
            return WorkoutType.valueOf(normalizedType);
        } catch (IllegalArgumentException e) {
            // If direct conversion fails, try to find by display name
            for (WorkoutType workoutType : WorkoutType.values()) {
                if (workoutType.getDisplayName().equalsIgnoreCase(type.trim())) {
                    return workoutType;
                }
            }
            throw new IllegalArgumentException("Unknown workout type: " + type +
                ". Valid types are: " + java.util.Arrays.toString(WorkoutType.values()));
        }
    }
}
