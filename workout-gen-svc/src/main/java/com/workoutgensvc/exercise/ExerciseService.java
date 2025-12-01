package com.workoutgensvc.exercise;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.workoutgensvc.core.AIService;
import com.workoutgensvc.exercise.enums.ExerciseType;
import com.workoutgensvc.exercise.enums.Intensity;
import com.workoutgensvc.exercise.enums.MuscleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final AIService aiService;
    private final Gson gson;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository, AIService aiService, Gson gson) {
        this.exerciseRepository = exerciseRepository;
        this.aiService = aiService;
        this.gson = gson;
    }

    @Transactional
    public Exercise generateExercise(String muscleGroup, String difficulty, String equipment) {
        try {
            String response = aiService.generateValidExercise(muscleGroup, difficulty, equipment, 3);
            response = response.replaceAll("```", "").replace("json\n", "");
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

            Exercise exercise = Exercise.builder()
                    .name(jsonObject.get("name").getAsString())
                    .type(ExerciseType.valueOf(jsonObject.get("type").getAsString()))
                    .imageUrl(jsonObject.has("imageUrl") && !jsonObject.get("imageUrl").getAsString().trim().isEmpty() ?
                        jsonObject.get("imageUrl").getAsString() : null)
                    .videoUrl(jsonObject.has("videoUrl") && !jsonObject.get("videoUrl").getAsString().trim().isEmpty() ?
                        jsonObject.get("videoUrl").getAsString() : null)
                    .build();

            if (jsonObject.has("muscleTargets")) {
                List<MuscleTarget> muscleTargets = new ArrayList<>();
                JsonArray muscleTargetsArray = jsonObject.getAsJsonArray("muscleTargets");

                for (JsonElement muscleTargetElement : muscleTargetsArray) {
                    try {
                        JsonObject jsonMuscleTarget = muscleTargetElement.getAsJsonObject();
                        MuscleTarget muscleTarget = new MuscleTarget();

                        if (jsonMuscleTarget.has("muscle")) {
                            muscleTarget.setMuscle(MuscleType.valueOf(jsonMuscleTarget.get("muscle").getAsString()));
                        }

                        if (jsonMuscleTarget.has("intensity")) {
                            muscleTarget.setIntensity(Intensity.valueOf(jsonMuscleTarget.get("intensity").getAsString()));
                        } else {
                            muscleTarget.setIntensity(Intensity.MODERATE);
                        }

                        muscleTargets.add(muscleTarget);
                    } catch (Exception e) {
                        log.warn("Failed to parse muscle target: {}", muscleTargetElement, e);
                    }
                }
                exercise.setMuscleGroupTarget(muscleTargets);
            }

            if ((exercise.getImageUrl() == null || exercise.getImageUrl().trim().isEmpty()) &&
                (exercise.getVideoUrl() == null || exercise.getVideoUrl().trim().isEmpty())) {
                throw new IllegalArgumentException("Exercise must have at least one media URL (imageUrl or videoUrl)");
            }

            return exerciseRepository.save(exercise);

        } catch (Exception e) {
            log.error("Failed to parse AI response for exercise generation", e);
            throw new RuntimeException("Failed to generate exercise from AI response: " + e.getMessage(), e);
        }
    }

    public List<Exercise> generateMultipleExercises(String muscleGroup, String difficulty, String equipment, int count) {
        List<Exercise> exercises = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                exercises.add(generateExercise(muscleGroup, difficulty, equipment));
            } catch (Exception e) {
                log.warn("Failed to generate exercise {} of {}", i + 1, count, e);
            }
        }
        return exercises;
    }

    @Transactional(readOnly = true)
    public String getAllNames() {
        List<String> exercises = exerciseRepository.findAll()
                .stream().map(Exercise::getName)
                .toList();

        return exercises.isEmpty()
                ? "No existing exercises available - you can suggest common exercises"
                : "Available exercises: " + String.join(", ", exercises);
    }

    @Transactional(readOnly = true)
    public Optional<Exercise> getByName(String name) {
        List<Exercise> exercises = exerciseRepository.findByNameIgnoreCase(name);
        if (exercises.isEmpty()) {
            return Optional.empty();
        } else if (exercises.size() == 1) {
            return Optional.of(exercises.get(0));
        } else {
            // Multiple exercises with same name - return the first one
            log.warn("Multiple exercises found with name '{}', returning first match: {}", name, exercises.get(0).getName());
            return Optional.of(exercises.get(0));
        }
    }
}
