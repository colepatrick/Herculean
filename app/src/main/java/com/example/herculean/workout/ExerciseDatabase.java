package com.example.herculean.workout;

import com.example.herculean.datahandling.GlobalData;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDatabase {

    private static List<Workout> exercises = null;

    public static List<Workout> getAllExercises() {
        if (exercises == null) {
            initializeExercises();
        }
        return new ArrayList<>(exercises);
    }

    private static void initializeExercises() {
        exercises = new ArrayList<>();

        // Chest
        exercises.add(new Strength("Bench Press", "Chest", 0, 0, 0));
        exercises.add(new Strength("Incline Bench Press", "Chest", 0, 0, 0));
        exercises.add(new Strength("Decline Bench Press", "Chest", 0, 0, 0));
        exercises.add(new Strength("Dumbbell Flyes", "Chest", 0, 0, 0));
        exercises.add(new Bodyweight("Push-ups", "Chest", 0, 0));
        exercises.add(new Strength("Cable Crossover", "Chest", 0, 0, 0));
        exercises.add(new Strength("Chest Press Machine", "Chest", 0, 0, 0));
        exercises.add(new Bodyweight("Dips", "Chest", 0, 0));
        exercises.add(new Strength("Pec Deck", "Chest", 0, 0, 0));

        // Back
        exercises.add(new Strength("Deadlift", "Back", 0, 0, 0));
        exercises.add(new Bodyweight("Pull-ups", "Back", 0, 0));
        exercises.add(new Strength("Lat Pulldown", "Back", 0, 0, 0));
        exercises.add(new Strength("Bent Over Row", "Back", 0, 0, 0));
        exercises.add(new Strength("T-Bar Row", "Back", 0, 0, 0));
        exercises.add(new Strength("Seated Cable Row", "Back", 0, 0, 0));
        exercises.add(new Strength("Face Pulls", "Back", 0, 0, 0));
        exercises.add(new Bodyweight("Hyperextensions", "Back", 0, 0));
        exercises.add(new Strength("Single Arm Dumbbell Row", "Back", 0, 0, 0));
        exercises.add(new Strength("Shrugs", "Back", 0, 0, 0));

        // Leg
        exercises.add(new Strength("Squat", "Legs", 0, 0, 0));
        exercises.add(new Strength("Front Squat", "Legs", 0, 0, 0));
        exercises.add(new Strength("Leg Press", "Legs", 0, 0, 0));
        exercises.add(new Strength("Leg Extension", "Legs", 0, 0, 0));
        exercises.add(new Strength("Leg Curl", "Legs", 0, 0, 0));
        exercises.add(new Bodyweight("Lunges", "Legs", 0, 0));
        exercises.add(new Strength("Bulgarian Split Squat", "Legs", 0, 0, 0));
        exercises.add(new Bodyweight("Calf Raises", "Legs", 0, 0));
        exercises.add(new Strength("Romanian Deadlift", "Legs", 0, 0, 0));
        exercises.add(new Strength("Hack Squat", "Legs", 0, 0, 0));
        exercises.add(new Bodyweight("Box Jumps", "Legs", 0, 0));
        exercises.add(new Bodyweight("Step-ups", "Legs", 0, 0));

        // Shoulder
        exercises.add(new Strength("Overhead Press", "Shoulders", 0, 0, 0));
        exercises.add(new Strength("Dumbbell Shoulder Press", "Shoulders", 0, 0, 0));
        exercises.add(new Strength("Lateral Raises", "Shoulders", 0, 0, 0));
        exercises.add(new Strength("Front Raises", "Shoulders", 0, 0, 0));
        exercises.add(new Strength("Rear Delt Flyes", "Shoulders", 0, 0, 0));
        exercises.add(new Strength("Arnold Press", "Shoulders", 0, 0, 0));
        exercises.add(new Strength("Upright Row", "Shoulders", 0, 0, 0));
        exercises.add(new Strength("Cable Lateral Raises", "Shoulders", 0, 0, 0));

        // Arm
        exercises.add(new Strength("Bicep Curls", "Arms", 0, 0, 0));
        exercises.add(new Strength("Hammer Curls", "Arms", 0, 0, 0));
        exercises.add(new Strength("Preacher Curls", "Arms", 0, 0, 0));
        exercises.add(new Strength("Concentration Curls", "Arms", 0, 0, 0));
        exercises.add(new Strength("Tricep Pushdown", "Arms", 0, 0, 0));
        exercises.add(new Strength("Skull Crushers", "Arms", 0, 0, 0));
        exercises.add(new Bodyweight("Tricep Dips", "Arms", 0, 0));
        exercises.add(new Strength("Overhead Tricep Extension", "Arms", 0, 0, 0));
        exercises.add(new Strength("Cable Curls", "Arms", 0, 0, 0));
        exercises.add(new Strength("Close Grip Bench Press", "Arms", 0, 0, 0));

        // Core
        exercises.add(new Bodyweight("Crunches", "Core", 0, 0));
        exercises.add(new Bodyweight("Planks", "Core", 0, 0));
        exercises.add(new Bodyweight("Russian Twists", "Core", 0, 0));
        exercises.add(new Bodyweight("Leg Raises", "Core", 0, 0));
        exercises.add(new Strength("Ab Wheel", "Core", 0, 0, 0));
        exercises.add(new Bodyweight("Mountain Climbers", "Core", 0, 0));
        exercises.add(new Bodyweight("Bicycle Crunches", "Core", 0, 0));
        exercises.add(new Bodyweight("Dead Bug", "Core", 0, 0));
        exercises.add(new Bodyweight("Side Planks", "Core", 0, 0));
        exercises.add(new Bodyweight("Hanging Leg Raises", "Core", 0, 0));

        // Cardio
        exercises.add(new Cardio("Running", "Cardio", 0, 0));
        exercises.add(new Cardio("Cycling", "Cardio", 0, 0));
        exercises.add(new Cardio("Rowing", "Cardio", 0, 0));
        exercises.add(new Cardio("Jump Rope", "Cardio", 0, 0));
        exercises.add(new Cardio("Stair Climber", "Cardio", 0, 0));
        exercises.add(new Cardio("Elliptical", "Cardio", 0, 0));
        exercises.add(new Cardio("Swimming", "Cardio", 0, 0));
        exercises.add(new Bodyweight("Burpees", "Cardio", 0, 0));
        exercises.add(new Cardio("Sprints", "Cardio", 0, 0));

        // Olympic
        exercises.add(new Strength("Clean and Jerk", "Olympic", 0, 0, 0));
        exercises.add(new Strength("Snatch", "Olympic", 0, 0, 0));
        exercises.add(new Strength("Power Clean", "Olympic", 0, 0, 0));
        exercises.add(new Strength("Hang Clean", "Olympic", 0, 0, 0));
        exercises.add(new Strength("Push Press", "Olympic", 0, 0, 0));
        exercises.add(new Strength("Clean Pull", "Olympic", 0, 0, 0));

        if (GlobalData.currentUser != null && GlobalData.currentUser.getCustomExercises() != null) {
            for(Workout workout : GlobalData.currentUser.getCustomExercises()) {
                exercises.add(workout);
            }
        }
    }

    public static void addCustomExercise(Workout workout) {
        if (exercises == null) {
            initializeExercises();
        }
        exercises.add(workout);
    }

    public static List<Workout> getExercisesByBodyPart(String bodyPart) {
        if (exercises == null) {
            initializeExercises();
        }
        List<Workout> filtered = new ArrayList<>();
        for (Workout workout : exercises) {
            if (workout.getBodyPart().equalsIgnoreCase(bodyPart)) {
                filtered.add(workout);
            }
        }
        return filtered;
    }
}