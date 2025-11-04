package com.example.herculean.workout;

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
        exercises.add(new Workout("Bench Press", "Chest"));
        exercises.add(new Workout("Incline Bench Press", "Chest"));
        exercises.add(new Workout("Decline Bench Press", "Chest"));
        exercises.add(new Workout("Dumbbell Flyes", "Chest"));
        exercises.add(new Workout("Push-ups", "Chest"));
        exercises.add(new Workout("Cable Crossover", "Chest"));
        exercises.add(new Workout("Chest Press Machine", "Chest"));
        exercises.add(new Workout("Dips", "Chest"));
        exercises.add(new Workout("Pec Deck", "Chest"));

        // Back
        exercises.add(new Workout("Deadlift", "Back"));
        exercises.add(new Workout("Pull-ups", "Back"));
        exercises.add(new Workout("Lat Pulldown", "Back"));
        exercises.add(new Workout("Bent Over Row", "Back"));
        exercises.add(new Workout("T-Bar Row", "Back"));
        exercises.add(new Workout("Seated Cable Row", "Back"));
        exercises.add(new Workout("Face Pulls", "Back"));
        exercises.add(new Workout("Hyperextensions", "Back"));
        exercises.add(new Workout("Single Arm Dumbbell Row", "Back"));
        exercises.add(new Workout("Shrugs", "Back"));

        // Leg
        exercises.add(new Workout("Squat", "Legs"));
        exercises.add(new Workout("Front Squat", "Legs"));
        exercises.add(new Workout("Leg Press", "Legs"));
        exercises.add(new Workout("Leg Extension", "Legs"));
        exercises.add(new Workout("Leg Curl", "Legs"));
        exercises.add(new Workout("Lunges", "Legs"));
        exercises.add(new Workout("Bulgarian Split Squat", "Legs"));
        exercises.add(new Workout("Calf Raises", "Legs"));
        exercises.add(new Workout("Romanian Deadlift", "Legs"));
        exercises.add(new Workout("Hack Squat", "Legs"));
        exercises.add(new Workout("Box Jumps", "Legs"));
        exercises.add(new Workout("Step-ups", "Legs"));

        // Shoulder
        exercises.add(new Workout("Overhead Press", "Shoulders"));
        exercises.add(new Workout("Dumbbell Shoulder Press", "Shoulders"));
        exercises.add(new Workout("Lateral Raises", "Shoulders"));
        exercises.add(new Workout("Front Raises", "Shoulders"));
        exercises.add(new Workout("Rear Delt Flyes", "Shoulders"));
        exercises.add(new Workout("Arnold Press", "Shoulders"));
        exercises.add(new Workout("Upright Row", "Shoulders"));
        exercises.add(new Workout("Cable Lateral Raises", "Shoulders"));

        // Arm
        exercises.add(new Workout("Bicep Curls", "Arms"));
        exercises.add(new Workout("Hammer Curls", "Arms"));
        exercises.add(new Workout("Preacher Curls", "Arms"));
        exercises.add(new Workout("Concentration Curls", "Arms"));
        exercises.add(new Workout("Tricep Pushdown", "Arms"));
        exercises.add(new Workout("Skull Crushers", "Arms"));
        exercises.add(new Workout("Tricep Dips", "Arms"));
        exercises.add(new Workout("Overhead Tricep Extension", "Arms"));
        exercises.add(new Workout("Cable Curls", "Arms"));
        exercises.add(new Workout("Close Grip Bench Press", "Arms"));

        // Core
        exercises.add(new Workout("Crunches", "Core"));
        exercises.add(new Workout("Planks", "Core"));
        exercises.add(new Workout("Russian Twists", "Core"));
        exercises.add(new Workout("Leg Raises", "Core"));
        exercises.add(new Workout("Ab Wheel", "Core"));
        exercises.add(new Workout("Mountain Climbers", "Core"));
        exercises.add(new Workout("Bicycle Crunches", "Core"));
        exercises.add(new Workout("Dead Bug", "Core"));
        exercises.add(new Workout("Side Planks", "Core"));
        exercises.add(new Workout("Hanging Leg Raises", "Core"));

        // Cardio
        exercises.add(new Workout("Running", "Cardio"));
        exercises.add(new Workout("Cycling", "Cardio"));
        exercises.add(new Workout("Rowing", "Cardio"));
        exercises.add(new Workout("Jump Rope", "Cardio"));
        exercises.add(new Workout("Stair Climber", "Cardio"));
        exercises.add(new Workout("Elliptical", "Cardio"));
        exercises.add(new Workout("Swimming", "Cardio"));
        exercises.add(new Workout("Burpees", "Cardio"));
        exercises.add(new Workout("Sprints", "Cardio"));

        // Olympic
        exercises.add(new Workout("Clean and Jerk", "Olympic"));
        exercises.add(new Workout("Snatch", "Olympic"));
        exercises.add(new Workout("Power Clean", "Olympic"));
        exercises.add(new Workout("Hang Clean", "Olympic"));
        exercises.add(new Workout("Push Press", "Olympic"));
        exercises.add(new Workout("Clean Pull", "Olympic"));
    }

    // soon
    public static void addCustomExercise(String exerciseName, String bodyPart) {
        return;
    }

    // soon
    public static List<Workout> getExercisesByBodyPart(String bodyPart) {
        return exercises;
    }
}